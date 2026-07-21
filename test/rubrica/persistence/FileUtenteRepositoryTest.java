package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import rubrica.domain.Utente;

class FileUtenteRepositoryTest {

    @TempDir
    Path tmp;

    private File file() {
        return tmp.resolve("utenti.txt").toFile();
    }

    private List<String> righeSuDisco() throws IOException {
        return Files.readAllLines(file().toPath(), StandardCharsets.UTF_8);
    }

    @Test
    void fileAssenteRestituisceListaVuotaSenzaErrori() {
        assertTrue(new FileUtenteRepository(file()).loadAll().isEmpty());
    }

    @Test
    void insertAssegnaIdEScriveUnaRigaSenzaPasswordInChiaro() throws IOException {
        FileUtenteRepository repo = new FileUtenteRepository(file());
        repo.loadAll();

        Utente u = Utente.nuovo("mario", "segreta");
        repo.insert(u);

        assertEquals(1, u.getId());
        assertEquals(1, righeSuDisco().size());
        assertTrue(righeSuDisco().get(0).startsWith("mario;"));
        assertFalse(righeSuDisco().get(0).contains("segreta"));
    }

    @Test
    void insertMultipliMantengonoIdCrescenti() {
        FileUtenteRepository repo = new FileUtenteRepository(file());
        repo.loadAll();

        Utente a = Utente.nuovo("mario", "a");
        Utente b = Utente.nuovo("luigi", "b");
        repo.insert(a);
        repo.insert(b);

        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
    }

    @Test
    void gliUtentiPersistonoTraIstanzeDiverse() {
        FileUtenteRepository primo = new FileUtenteRepository(file());
        primo.loadAll();
        primo.insert(Utente.nuovo("mario", "segreta"));

        List<Utente> ricaricati = new FileUtenteRepository(file()).loadAll();

        assertEquals(1, ricaricati.size());
        assertEquals("mario", ricaricati.get(0).getUsername());
        assertEquals(1, ricaricati.get(0).getId());
        assertTrue(ricaricati.get(0).passwordCorretta("segreta"));
    }

    @Test
    void updateRiscriveSoloLUtenteIndividuato() {
        FileUtenteRepository repo = new FileUtenteRepository(file());
        repo.loadAll();
        repo.insert(Utente.nuovo("mario", "a"));
        Utente luigi = Utente.nuovo("luigi", "b");
        repo.insert(luigi);

        luigi.cambiaPassword("nuova");
        repo.update(luigi);

        List<Utente> ricaricati = new FileUtenteRepository(file()).loadAll();
        assertEquals(2, ricaricati.size());
        assertTrue(ricaricati.get(0).passwordCorretta("a"));
        assertTrue(ricaricati.get(1).passwordCorretta("nuova"));
    }

    @Test
    void updateDiUnIdInesistenteLanciaEccezione() {
        FileUtenteRepository repo = new FileUtenteRepository(file());
        repo.loadAll();
        Utente fantasma = new Utente(42, "mario", Utente.nuovo("mario", "a").getPasswordHash());

        assertThrows(IllegalArgumentException.class, () -> repo.update(fantasma));
    }

    @Test
    void loadAllSaltaLeRigheMalformate() throws IOException {
        FileUtenteRepository primo = new FileUtenteRepository(file());
        primo.loadAll();
        primo.insert(Utente.nuovo("mario", "a"));
        Files.write(file().toPath(), List.of("riga rotta"), StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);

        List<Utente> ricaricati = new FileUtenteRepository(file()).loadAll();

        assertEquals(1, ricaricati.size());
        assertEquals("mario", ricaricati.get(0).getUsername());
    }
}
