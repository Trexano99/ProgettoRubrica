package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rubrica.domain.Utente;

/**
 * Test di integrazione di {@link MysqlUtenteRepository} su H2 in modalita' MySQL:
 * la stessa SQL che girerebbe su un vero MySQL, senza bisogno di un server.
 */
class MysqlUtenteRepositoryTest {

    private H2Database db;
    private MysqlUtenteRepository repository;

    @BeforeEach
    void setUp() {
        db = new H2Database();
        db.creaSchema();
        repository = new MysqlUtenteRepository(db);
    }

    @Test
    void loadAllSuTabellaVuotaRitornaListaVuota() {
        assertTrue(repository.loadAll().isEmpty());
    }

    @Test
    void insertAssegnaLIdGeneratoEPersiste() {
        Utente u = Utente.nuovo("mario", "segreta");
        repository.insert(u);

        assertNotNull(u.getId());

        List<Utente> tutti = repository.loadAll();
        assertEquals(1, tutti.size());
        assertEquals("mario", tutti.get(0).getUsername());
        assertTrue(tutti.get(0).passwordCorretta("segreta"));
    }

    @Test
    void gliIdSonoProgressiviEDistinti() {
        Utente primo = Utente.nuovo("mario", "a");
        Utente secondo = Utente.nuovo("luigi", "b");
        repository.insert(primo);
        repository.insert(secondo);

        assertNotEquals(primo.getId(), secondo.getId());
        assertEquals(2, repository.loadAll().size());
    }

    @Test
    void updateModificaUsernameEPassword() {
        Utente u = Utente.nuovo("mario", "vecchia");
        repository.insert(u);

        Utente modificato = new Utente(u.getId(), "super-mario", u.getPasswordHash());
        modificato.cambiaPassword("nuova");
        repository.update(modificato);

        List<Utente> tutti = repository.loadAll();
        assertEquals(1, tutti.size());
        assertEquals("super-mario", tutti.get(0).getUsername());
        assertTrue(tutti.get(0).passwordCorretta("nuova"));
        assertFalse(tutti.get(0).passwordCorretta("vecchia"));
    }

    @Test
    void updateDiUnIdInesistenteLanciaEccezione() {
        Utente fantasma = new Utente(999, "nessuno", Utente.nuovo("x", "y").getPasswordHash());
        assertThrows(IllegalArgumentException.class, () -> repository.update(fantasma));
    }

    @Test
    void laPasswordNonEMaiSalvataInChiaro() {
        Utente u = Utente.nuovo("mario", "top-secret");
        repository.insert(u);
        // Rileggendo, la verifica funziona ma il valore in chiaro non e' recuperabile.
        Utente riletto = repository.loadAll().get(0);
        assertTrue(riletto.passwordCorretta("top-secret"));
        assertFalse(riletto.passwordCorretta("altro"));
    }
}
