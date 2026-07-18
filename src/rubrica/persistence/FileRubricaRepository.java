package rubrica.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import rubrica.domain.Persona;

/**
 * Salvataggio della rubrica su un singolo file di testo (per default
 * {@code informazioni.txt}), nel formato definito da {@link PersonaCodec}.
 *
 * <p>Serve per mantenere un mirror ordinato in memoria che rispecchia 
 * il contenuto del file. L'{@code id} surrogato
 * viene assegnato per posizione al caricamento e <strong>non</strong> viene
 * scritto sul file. {@link #insert(Persona)} accoda una riga 
 * {@link #update(Persona)} e {@link #delete(Persona)} riscrivono
 * l'intero file.</p>
 */
public class FileRubricaRepository implements RubricaRepository {

    private final File file;
    private final List<Persona> current = new ArrayList<>();
    private int counter = 0;
    private boolean loaded = false;

    /**
     * @param file il file che identifica lo storage (puo' non esistere ancora)
     */
    public FileRubricaRepository(File file) {
        this.file = file;
    }

    @Override
    public List<Persona> loadAll() {
        current.clear();
        counter = 0;
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name())) {
                while (scanner.hasNextLine()) {
                    String riga = scanner.nextLine();
                    if (riga.isEmpty()) continue;
                    try {
                        Persona p = PersonaCodec.fromLine(riga);
                        p.setId(++counter);
                        current.add(p);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Riga ignorata (malformata): " + riga);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }
        loaded = true;
        return new ArrayList<>(current);
    }

    @Override
    public void insert(Persona p) {
        ensureLoaded();
        p.setId(++counter);
        current.add(p);
        appendLine(p);
    }

    @Override
    public void update(Persona p) {
        ensureLoaded();
        int index = indexOfId(p.getId());
        if (index < 0) 
            throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());
        current.set(index, p);
        rewriteAll();
    }

    @Override
    public void delete(Persona p) {
        ensureLoaded();
        int index = indexOfId(p.getId());
        if (index < 0) 
            throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());
        current.remove(index);
        rewriteAll();
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

    private void appendLine(Persona p) {
        ensureParentDir();
        try (PrintStream out = new PrintStream(
                new FileOutputStream(file, true), true, StandardCharsets.UTF_8.name())) {
            out.println(PersonaCodec.toLine(p));
        } catch (IOException e) {
            throw new UncheckedIOException("Errore in scrittura su " + file, e);
        }
    }

    private void rewriteAll() {
        ensureParentDir();
        try (PrintStream out = new PrintStream(
                new FileOutputStream(file, false), true, StandardCharsets.UTF_8.name())) {
            for (Persona p : current) 
                out.println(PersonaCodec.toLine(p));
        } catch (IOException e) {
            throw new UncheckedIOException("Errore in scrittura su " + file, e);
        }
    }

    private void ensureParentDir() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) 
            parent.mkdirs();
    }
}
