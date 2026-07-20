package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import rubrica.domain.Persona;

class DirectoryRubricaRepositoryTest {

    @TempDir
    Path tmp;

    private File directory() {
        return tmp.resolve("informazioni").toFile();
    }

    private List<String> nomiFile() {
        String[] nomi = directory().list();
        if (nomi == null) {
            return List.of();
        }
        Arrays.sort(nomi);
        return List.of(nomi);
    }

    private String contenuto(String nomeFile) throws IOException {
        return Files.readAllLines(new File(directory(), nomeFile).toPath(),
                StandardCharsets.UTF_8).get(0);
    }

    private void scriviContatto(String nomeFile, String riga) throws IOException {
        Files.createDirectories(directory().toPath());
        Files.write(new File(directory(), nomeFile).toPath(), List.of(riga),
                StandardCharsets.UTF_8);
    }

    private Persona steve() {
        return new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
    }

    @Test
    void cartellaAssenteRestituisceListaVuotaSenzaErrori() {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        assertTrue(repo.loadAll().isEmpty());
    }

    @Test
    void insertCreaUnFileNomeCognomePerContatto() throws IOException {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();

        Persona p = steve();
        repo.insert(p);

        assertEquals(1, p.getId());
        assertEquals(List.of("Steve-Jobs.txt"), nomiFile());
        assertEquals("Steve;Jobs;via Cupertino 13;0612344;56", contenuto("Steve-Jobs.txt"));
    }

    @Test
    void omonimiRicevonoUnSuffissoNumericoProgressivo() {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();

        repo.insert(new Persona("Mario", "Rossi", "via Roma 1", "111", 30));
        repo.insert(new Persona("Mario", "Rossi", "via Milano 2", "222", 40));
        repo.insert(new Persona("Mario", "Rossi", "via Napoli 3", "333", 50));

        assertEquals(List.of("Mario-Rossi-2.txt", "Mario-Rossi-3.txt", "Mario-Rossi.txt"),
                nomiFile());
    }

    @Test
    void omonimiRestanoDistintiDopoIlRicaricamento() {
        DirectoryRubricaRepository primo = new DirectoryRubricaRepository(directory());
        primo.loadAll();
        primo.insert(new Persona("Mario", "Rossi", "via Roma 1", "111", 30));
        primo.insert(new Persona("Mario", "Rossi", "via Milano 2", "222", 40));

        List<Persona> ricaricate = new DirectoryRubricaRepository(directory()).loadAll();

        assertEquals(2, ricaricate.size());
        assertEquals(List.of("via Roma 1", "via Milano 2"),
                List.of(ricaricate.get(0).getIndirizzo(), ricaricate.get(1).getIndirizzo()));
    }

    @Test
    void loadAllAssegnaIdProgressiviPerPosizione() throws IOException {
        scriviContatto("Bill-Gates.txt", "Bill;Gates;via Redmond 10;06688989;60");
        scriviContatto("Steve-Jobs.txt", "Steve;Jobs;via Cupertino 13;0612344;56");

        List<Persona> persone = new DirectoryRubricaRepository(directory()).loadAll();

        assertEquals(2, persone.size());
        assertEquals(1, persone.get(0).getId());
        assertEquals("Bill", persone.get(0).getNome());
        assertEquals(2, persone.get(1).getId());
        assertEquals("Steve", persone.get(1).getNome());
    }

    @Test
    void updateSenzaCambioNomeRiscriveLoStessoFile() throws IOException {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();
        Persona p = steve();
        repo.insert(p);

        p.setTelefono("999");
        repo.update(p);

        assertEquals(List.of("Steve-Jobs.txt"), nomiFile());
        assertEquals("Steve;Jobs;via Cupertino 13;999;56", contenuto("Steve-Jobs.txt"));
    }

    @Test
    void updateConCambioNomeRinominaIlFile() throws IOException {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();
        Persona p = steve();
        repo.insert(p);

        p.setCognome("Wozniak");
        repo.update(p);

        assertEquals(List.of("Steve-Wozniak.txt"), nomiFile());
        assertEquals("Steve;Wozniak;via Cupertino 13;0612344;56", contenuto("Steve-Wozniak.txt"));
    }

    @Test
    void updateDiUnOmonimoNonTraslocaSuUnFileGiaOccupato() throws IOException {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();
        repo.insert(new Persona("Mario", "Rossi", "via Roma 1", "111", 30));
        Persona secondo = new Persona("Mario", "Rossi", "via Milano 2", "222", 40);
        repo.insert(secondo);

        secondo.setTelefono("999");
        repo.update(secondo);

        assertEquals(List.of("Mario-Rossi-2.txt", "Mario-Rossi.txt"), nomiFile());
        assertEquals("Mario;Rossi;via Roma 1;111;30", contenuto("Mario-Rossi.txt"));
        assertEquals("Mario;Rossi;via Milano 2;999;40", contenuto("Mario-Rossi-2.txt"));
    }

    @Test
    void deleteRimuoveSoloIlFileDelContatto() {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();
        repo.insert(steve());
        Persona bill = new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60);
        repo.insert(bill);

        repo.delete(bill);

        assertEquals(List.of("Steve-Jobs.txt"), nomiFile());
    }

    @Test
    void loadAllIgnoraIFileMalformati() throws IOException {
        scriviContatto("Steve-Jobs.txt", "Steve;Jobs;via Cupertino 13;0612344;56");
        scriviContatto("Rotto.txt", "riga;rotta");

        List<Persona> persone = new DirectoryRubricaRepository(directory()).loadAll();

        assertEquals(1, persone.size());
        assertEquals("Steve", persone.get(0).getNome());
    }

    @Test
    void caratteriNonAmmessiNelNomeVengonoSostituiti() {
        DirectoryRubricaRepository repo = new DirectoryRubricaRepository(directory());
        repo.loadAll();

        repo.insert(new Persona("Anna Maria", "De/Luca", "via Verdi 4", "555", 28));

        assertEquals(List.of("Anna_Maria-De_Luca.txt"), nomiFile());
    }

    @Test
    void insertPersisteTraIstanzeDiverse() {
        DirectoryRubricaRepository primo = new DirectoryRubricaRepository(directory());
        primo.loadAll();
        primo.insert(steve());

        List<Persona> ricaricate = new DirectoryRubricaRepository(directory()).loadAll();

        assertEquals(1, ricaricate.size());
        assertEquals("Steve", ricaricate.get(0).getNome());
    }
}
