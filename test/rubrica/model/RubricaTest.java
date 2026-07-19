package rubrica.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rubrica.domain.Persona;

class RubricaTest {

    private InMemoryRubricaRepository repo;
    private Rubrica rubrica;

    @BeforeEach
    void setUp() {
        repo = new InMemoryRubricaRepository();
        rubrica = new Rubrica(repo);
    }

    private Persona steve() {
        return new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
    }

    private Persona bill() {
        return new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60);
    }

    @Test
    void loadPopolaLaListaDalRepository() {
        repo.insert(steve());
        repo.insert(bill());

        rubrica.load();

        assertEquals(2, rubrica.size());
        assertEquals("Steve", rubrica.get(0).getNome());
        assertEquals("Bill", rubrica.get(1).getNome());
    }

    @Test
    void addDelegaInsertEAggiungeAllaLista() {
        Persona p = steve();
        rubrica.add(p);

        assertEquals(1, repo.insertCount);
        assertEquals(1, rubrica.size());
        assertEquals(p, rubrica.get(0));
        assertNotNull(p.getId(), "insert deve assegnare l'id");
    }

    @Test
    void updateDelegaUpdateERifletteIlCambiamentoNellaLista() {
        Persona p = steve();
        rubrica.add(p);

        Persona modificata = new Persona(p.getId(), "Steve", "Jobs", "via Cupertino 13", "999", 56);
        rubrica.update(modificata);

        assertEquals(1, repo.updateCount);
        assertEquals(1, rubrica.size());
        assertEquals("999", rubrica.get(0).getTelefono());
    }

    @Test
    void deleteDelegaDeleteERimuoveDallaLista() {
        Persona a = steve();
        Persona b = bill();
        rubrica.add(a);
        rubrica.add(b);

        rubrica.delete(a);

        assertEquals(1, repo.deleteCount);
        assertEquals(1, rubrica.size());
        assertEquals("Bill", rubrica.get(0).getNome());
    }

    @Test
    void deletePersisteAncheNelRepository() {
        Persona a = steve();
        rubrica.add(a);
        rubrica.delete(a);
        assertEquals(0, repo.size());
    }

    @Test
    void getAllRestituisceTuttiIContatti() {
        rubrica.add(steve());
        rubrica.add(bill());
        assertEquals(2, rubrica.getAll().size());
    }

    @Test
    void addConTelefonoVuotoLanciaEccezioneENonPersiste() {
        Persona senzaTelefono = new Persona("Steve", "Jobs", "via Cupertino 13", "  ", 56);
        assertThrows(IllegalArgumentException.class, () -> rubrica.add(senzaTelefono));
        assertEquals(0, repo.insertCount, "non deve inserire nel repository");
        assertEquals(0, rubrica.size());
    }

    @Test
    void addConTelefonoNulloLanciaEccezione() {
        Persona senzaTelefono = new Persona("Steve", "Jobs", "via Cupertino 13", null, 56);
        assertThrows(IllegalArgumentException.class, () -> rubrica.add(senzaTelefono));
    }

    @Test
    void updateConTelefonoVuotoLanciaEccezione() {
        Persona p = steve();
        rubrica.add(p);
        Persona modificata = new Persona(p.getId(), "Steve", "Jobs", "via Cupertino 13", "", 56);
        assertThrows(IllegalArgumentException.class, () -> rubrica.update(modificata));
    }
}
