package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rubrica.domain.Persona;

/**
 * Test di integrazione di {@link MysqlRubricaRepository} su H2 in modalita'
 * MySQL: la stessa SQL che girerebbe su un vero MySQL, senza un server.
 */
class MysqlRubricaRepositoryTest {

    private H2Database db;
    private MysqlRubricaRepository repository;

    @BeforeEach
    void setUp() {
        db = new H2Database();
        db.creaSchema();
        repository = new MysqlRubricaRepository(db);
    }

    private Persona jobs() {
        return new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
    }

    @Test
    void loadAllSuTabellaVuotaRitornaListaVuota() {
        assertTrue(repository.loadAll().isEmpty());
    }

    @Test
    void insertAssegnaLIdGeneratoEPersisteIDati() {
        Persona p = jobs();
        repository.insert(p);

        assertNotNull(p.getId());

        List<Persona> tutti = repository.loadAll();
        assertEquals(1, tutti.size());
        assertEquals(jobs(), tutti.get(0));
        assertEquals(p.getId(), tutti.get(0).getId());
    }

    @Test
    void updateModificaIlContattoEsistente() {
        Persona p = jobs();
        repository.insert(p);

        Persona modificato = new Persona(p.getId(), "Steve", "Jobs",
                "via Infinite Loop 1", "0612344", 57);
        repository.update(modificato);

        List<Persona> tutti = repository.loadAll();
        assertEquals(1, tutti.size());
        assertEquals("via Infinite Loop 1", tutti.get(0).getIndirizzo());
        assertEquals(57, tutti.get(0).getEta());
    }

    @Test
    void deleteRimuoveSoloIlContattoIndicato() {
        Persona primo = jobs();
        Persona secondo = new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60);
        repository.insert(primo);
        repository.insert(secondo);

        repository.delete(primo);

        List<Persona> tutti = repository.loadAll();
        assertEquals(1, tutti.size());
        assertEquals("Gates", tutti.get(0).getCognome());
    }

    @Test
    void updateDiUnIdInesistenteLanciaEccezione() {
        Persona fantasma = new Persona(999, "Nessuno", "Ovunque", "", "000", 1);
        assertThrows(IllegalArgumentException.class, () -> repository.update(fantasma));
    }

    @Test
    void deleteDiUnIdInesistenteLanciaEccezione() {
        Persona fantasma = new Persona(999, "Nessuno", "Ovunque", "", "000", 1);
        assertThrows(IllegalArgumentException.class, () -> repository.delete(fantasma));
    }

    @Test
    void lOrdineDiCaricamentoSegueLId() {
        repository.insert(jobs());
        repository.insert(new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60));
        repository.insert(new Persona("Babbo", "Natale", "Polo Nord", "00000111", 99));

        List<Persona> tutti = repository.loadAll();
        assertEquals("Jobs", tutti.get(0).getCognome());
        assertEquals("Gates", tutti.get(1).getCognome());
        assertEquals("Natale", tutti.get(2).getCognome());
    }
}
