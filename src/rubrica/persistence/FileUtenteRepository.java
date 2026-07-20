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
import rubrica.domain.Utente;

/**
 * Salvataggio degli utenti su un singolo file di testo (per default
 * {@code utenti.txt}), nel formato definito da {@link UtenteCodec}.
 *
 * <p>Stessa struttura di {@link FileRubricaRepository}: mirror ordinato in
 * memoria, {@code id} assegnato per posizione al caricamento e non scritto su
 * disco, {@link #insert(Utente)} che accoda una riga e {@link #update(Utente)}
 * che riscrive l'intero file.</p>
 */
public class FileUtenteRepository implements UtenteRepository {

    private final File file;
    private final List<Utente> current = new ArrayList<>();
    private int counter = 0;
    private boolean loaded = false;

    /**
     * @param file il file che identifica lo storage (puo' non esistere ancora)
     */
    public FileUtenteRepository(File file) {
        this.file = file;
    }

    @Override
    public List<Utente> loadAll() {
        current.clear();
        counter = 0;
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name())) {
                while (scanner.hasNextLine()) {
                    String riga = scanner.nextLine();
                    if (riga.isEmpty()) continue;
                    try {
                        Utente u = UtenteCodec.fromLine(riga);
                        u.setId(++counter);
                        current.add(u);
                    } catch (IllegalArgumentException e) {
                        // La riga non viene stampata: conterrebbe l'hash della password.
                        System.err.println("Riga ignorata (utente malformato) in " + file.getName());
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
    public void insert(Utente u) {
        ensureLoaded();
        u.setId(++counter);
        current.add(u);
        appendLine(u);
    }

    @Override
    public void update(Utente u) {
        ensureLoaded();
        int index = indexOfId(u.getId());
        if (index < 0)
            throw new IllegalArgumentException("Utente non trovato, id=" + u.getId());
        current.set(index, u);
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

    private void appendLine(Utente u) {
        ensureParentDir();
        try (PrintStream out = new PrintStream(
                new FileOutputStream(file, true), true, StandardCharsets.UTF_8.name())) {
            out.println(UtenteCodec.toLine(u));
        } catch (IOException e) {
            throw new UncheckedIOException("Errore in scrittura su " + file, e);
        }
    }

    private void rewriteAll() {
        ensureParentDir();
        try (PrintStream out = new PrintStream(
                new FileOutputStream(file, false), true, StandardCharsets.UTF_8.name())) {
            for (Utente u : current)
                out.println(UtenteCodec.toLine(u));
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
