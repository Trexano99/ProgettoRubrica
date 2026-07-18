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
import rubrica.domain.Persona;

class FileRubricaRepositoryTest {

    @TempDir
    Path tmp;

    private File file() {
        return tmp.resolve("informazioni.txt").toFile();
    }

    private List<String> righeSuDisco() throws IOException {
        return Files.readAllLines(file().toPath(), StandardCharsets.UTF_8);
    }

    @Test
    void fileAssenteRestituisceListaVuotaSenzaErrori() {
        FileRubricaRepository repo = new FileRubricaRepository(file());
        assertTrue(repo.loadAll().isEmpty());
    }

    @Test
    void loadAllAssegnaIdProgressiviPerPosizione() throws IOException {
        Files.write(file().toPath(),
                List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "Bill;Gates;via Redmond 10;06688989;60"),
                StandardCharsets.UTF_8);

        List<Persona> persone = new FileRubricaRepository(file()).loadAll();

        assertEquals(2, persone.size());
        assertEquals(1, persone.get(0).getId());
        assertEquals("Steve", persone.get(0).getNome());
        assertEquals(2, persone.get(1).getId());
        assertEquals("Bill", persone.get(1).getNome());
    }

    @Test
    void insertAssegnaIdEAppendeLaRigaSenzaId() throws IOException {
        FileRubricaRepository repo = new FileRubricaRepository(file());
        repo.loadAll();

        Persona p = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        repo.insert(p);

        assertEquals(1, p.getId());
        assertEquals(List.of("Steve;Jobs;via Cupertino 13;0612344;56"), righeSuDisco());
    }

    @Test
    void insertMultipliMantengonoIdCrescenti() {
        FileRubricaRepository repo = new FileRubricaRepository(file());
        repo.loadAll();

        Persona a = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        Persona b = new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60);
        repo.insert(a);
        repo.insert(b);

        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
    }

    @Test
    void updateRiscriveSoloLaRigaDelContattoIndividuato() throws IOException {
        Files.write(file().toPath(),
                List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "Bill;Gates;via Redmond 10;06688989;60"),
                StandardCharsets.UTF_8);
        FileRubricaRepository repo = new FileRubricaRepository(file());
        List<Persona> persone = repo.loadAll();

        Persona bill = persone.get(1);
        bill.setTelefono("999");
        repo.update(bill);

        assertEquals(List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "Bill;Gates;via Redmond 10;999;60"),
                righeSuDisco());
    }

    @Test
    void deleteRimuoveLaRigaDelContatto() throws IOException {
        Files.write(file().toPath(),
                List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "Bill;Gates;via Redmond 10;06688989;60",
                        "Babbo;Natale;via del Polo Nord;00000111;99"),
                StandardCharsets.UTF_8);
        FileRubricaRepository repo = new FileRubricaRepository(file());
        List<Persona> persone = repo.loadAll();

        repo.delete(persone.get(1)); // Bill, quello di mezzo

        assertEquals(List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "Babbo;Natale;via del Polo Nord;00000111;99"),
                righeSuDisco());
    }

    @Test
    void loadAllSaltaLeRigheMalformate() throws IOException {
        Files.write(file().toPath(),
                List.of("Steve;Jobs;via Cupertino 13;0612344;56",
                        "riga;rotta",
                        "Bill;Gates;via Redmond 10;06688989;60"),
                StandardCharsets.UTF_8);

        List<Persona> persone = new FileRubricaRepository(file()).loadAll();

        assertEquals(2, persone.size());
        assertEquals("Steve", persone.get(0).getNome());
        assertEquals("Bill", persone.get(1).getNome());
    }

    @Test
    void insertPersisteTraIstanzeDiverse() {
        FileRubricaRepository primo = new FileRubricaRepository(file());
        primo.loadAll();
        primo.insert(new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56));

        List<Persona> ricaricate = new FileRubricaRepository(file()).loadAll();
        assertEquals(1, ricaricate.size());
        assertEquals("Steve", ricaricate.get(0).getNome());
    }
}
