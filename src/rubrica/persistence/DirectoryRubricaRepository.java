package rubrica.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import rubrica.domain.Persona;

/**
 * Salvataggio della rubrica su una cartella (per default {@code informazioni}),
 * con un file di testo per ogni contatto.
 *
 * <p>Il nome del file e' {@code Nome-Cognome.txt}; i caratteri non ammessi nei
 * nomi di file vengono sostituiti con {@code _}. Se due persone hanno lo stesso
 * nome e cognome, ai file successivi al primo viene aggiunto un suffisso
 * numerico progressivo: {@code Nome-Cognome-2.txt}, {@code Nome-Cognome-3.txt},
 * e cosi' via.</p>
 *
 * <p>Ogni file contiene una sola riga nel formato di {@link PersonaCodec}.
 * L'{@code id} surrogato viene assegnato per posizione al caricamento (i file
 * sono letti in ordine alfabetico di nome) e non viene scritto su disco: il
 * repository mantiene la corrispondenza {@code id -> file} in memoria, cosi'
 * che {@link #update(Persona)} e {@link #delete(Persona)} tocchino il solo file
 * del contatto interessato.</p>
 */
public class DirectoryRubricaRepository implements RubricaRepository {

    /** Estensione dei file dei contatti. */
    private static final String ESTENSIONE = ".txt";

    /** Caratteri non ammessi nei nomi di file (Windows compreso) e spazi. */
    private static final String CARATTERI_NON_AMMESSI = "[\\\\/:*?\"<>|\\s]";

    private final File directory;
    private final List<Persona> current = new ArrayList<>();
    private final Map<Integer, File> fileDiId = new LinkedHashMap<>();
    private int counter = 0;
    private boolean loaded = false;

    /**
     * @param directory la cartella che contiene i file dei contatti (puo' non
     *                  esistere ancora)
     */
    public DirectoryRubricaRepository(File directory) {
        this.directory = directory;
    }

    @Override
    public List<Persona> loadAll() {
        current.clear();
        fileDiId.clear();
        counter = 0;
        for (File f : fileDeiContatti()) {
            Persona p = leggiPersona(f);
            if (p == null) {
                continue;
            }
            p.setId(++counter);
            current.add(p);
            fileDiId.put(p.getId(), f);
        }
        loaded = true;
        return new ArrayList<>(current);
    }

    @Override
    public void insert(Persona p) {
        ensureLoaded();
        p.setId(++counter);
        File destinazione = fileLibero(p);
        scrivi(destinazione, p);
        current.add(p);
        fileDiId.put(p.getId(), destinazione);
    }

    @Override
    public void update(Persona p) {
        ensureLoaded();
        int index = indexOfId(p.getId());
        if (index < 0)
            throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());

        File vecchio = fileDiId.get(p.getId());
        // Se nome o cognome sono cambiati il file deve cambiare nome: si scrive
        // sul nuovo percorso e si elimina il vecchio.
        File atteso = nomeAttesoAncoraValido(vecchio, p) ? vecchio : fileLiberoEscludendo(p, vecchio);
        scrivi(atteso, p);
        if (!atteso.equals(vecchio))
            vecchio.delete();

        current.set(index, p);
        fileDiId.put(p.getId(), atteso);
    }

    @Override
    public void delete(Persona p) {
        ensureLoaded();
        int index = indexOfId(p.getId());
        if (index < 0)
            throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());

        File f = fileDiId.remove(p.getId());
        if (f != null)
            f.delete();
        current.remove(index);
    }

    private void ensureLoaded() {
        if (!loaded)
            loadAll();
    }

    private int indexOfId(Integer id) {
        for (int i = 0; i < current.size(); i++)
            if (current.get(i).getId().equals(id))
                return i;
        return -1;
    }

    /**
     * I file {@code .txt} della cartella, in ordine alfabetico di nome; fra
     * omonimi vale l'ordine del suffisso numerico, cosi' che
     * {@code Mario-Rossi.txt} preceda {@code Mario-Rossi-2.txt}.
     */
    private File[] fileDeiContatti() {
        File[] trovati = directory.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(ESTENSIONE));
        if (trovati == null)
            return new File[0];
        Arrays.sort(trovati, (a, b) -> {
            String baseA = baseDelFile(a.getName());
            String baseB = baseDelFile(b.getName());
            int confronto = baseA.compareToIgnoreCase(baseB);
            return confronto != 0
                    ? confronto
                    : Integer.compare(suffissoDelFile(a.getName()), suffissoDelFile(b.getName()));
        });
        return trovati;
    }

    /** Il nome del file senza estensione ne' suffisso numerico di omonimia. */
    private String baseDelFile(String nomeFile) {
        String senzaEstensione = senzaEstensione(nomeFile);
        int trattino = senzaEstensione.lastIndexOf('-');
        if (trattino > 0 && senzaEstensione.substring(trattino + 1).matches("\\d+"))
            return senzaEstensione.substring(0, trattino);
        return senzaEstensione;
    }

    /** Il suffisso numerico di omonimia del file ({@code 1} se assente). */
    private int suffissoDelFile(String nomeFile) {
        String senzaEstensione = senzaEstensione(nomeFile);
        int trattino = senzaEstensione.lastIndexOf('-');
        if (trattino > 0 && senzaEstensione.substring(trattino + 1).matches("\\d+"))
            return Integer.parseInt(senzaEstensione.substring(trattino + 1));
        return 1;
    }

    private String senzaEstensione(String nomeFile) {
        return nomeFile.toLowerCase().endsWith(ESTENSIONE)
                ? nomeFile.substring(0, nomeFile.length() - ESTENSIONE.length())
                : nomeFile;
    }

    /**
     * @return la persona contenuta nel file, oppure {@code null} se il file e'
     *         vuoto o malformato (segnalato su {@code stderr} e ignorato)
     */
    private Persona leggiPersona(File f) {
        try (Scanner scanner = new Scanner(f, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                String riga = scanner.nextLine();
                if (riga.isEmpty())
                    continue;
                return PersonaCodec.fromLine(riga);
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        } catch (IllegalArgumentException e) {
            System.err.println("File ignorato (contenuto malformato): " + f.getName());
            return null;
        }
    }

    private void scrivi(File destinazione, Persona p) {
        ensureDirectory();
        try (PrintStream out = new PrintStream(
                new FileOutputStream(destinazione, false), true, StandardCharsets.UTF_8.name())) {
            out.println(PersonaCodec.toLine(p));
        } catch (IOException e) {
            throw new UncheckedIOException("Errore in scrittura su " + destinazione, e);
        }
    }

    /**
     * Primo nome di file libero per la persona: {@code Nome-Cognome.txt}, poi
     * {@code Nome-Cognome-2.txt}, {@code Nome-Cognome-3.txt}, ...
     */
    private File fileLibero(Persona p) {
        return fileLiberoEscludendo(p, null);
    }

    /**
     * Come {@link #fileLibero(Persona)}, ma considera libero anche
     * {@code riutilizzabile} (il file gia' occupato dalla persona stessa).
     */
    private File fileLiberoEscludendo(Persona p, File riutilizzabile) {
        String base = baseNome(p);
        File candidato = new File(directory, base + ESTENSIONE);
        int suffisso = 1;
        while (occupato(candidato, riutilizzabile)) {
            suffisso++;
            candidato = new File(directory, base + "-" + suffisso + ESTENSIONE);
        }
        return candidato;
    }

    private boolean occupato(File candidato, File riutilizzabile) {
        if (!candidato.exists())
            return false;
        return !candidato.equals(riutilizzabile);
    }

    /** Vero se il file corrente e' gia' quello atteso per nome e cognome. */
    private boolean nomeAttesoAncoraValido(File attuale, Persona p) {
        if (attuale == null)
            return false;
        // Accetta sia "Nome-Cognome.txt" sia "Nome-Cognome-<numero>.txt".
        return baseDelFile(attuale.getName()).equals(baseNome(p));
    }

    /** {@code Nome-Cognome} ripulito dai caratteri non ammessi nei nomi di file. */
    private String baseNome(Persona p) {
        String base = sanitizza(p.getNome()) + "-" + sanitizza(p.getCognome());
        return base.equals("-") ? "Contatto" : base;
    }

    private String sanitizza(String valore) {
        if (valore == null)
            return "";
        return valore.trim().replaceAll(CARATTERI_NON_AMMESSI, "_");
    }

    private void ensureDirectory() {
        if (!directory.exists())
            directory.mkdirs();
    }
}
