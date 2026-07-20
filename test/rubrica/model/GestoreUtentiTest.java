package rubrica.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rubrica.domain.Utente;

class GestoreUtentiTest {

    private InMemoryUtenteRepository repository;
    private GestoreUtenti gestore;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUtenteRepository();
        gestore = new GestoreUtenti(repository);
        gestore.load();
    }

    @Test
    void allInizioNonCiSonoUtenti() {
        assertTrue(gestore.isVuoto());
        assertTrue(gestore.getAll().isEmpty());
    }

    @Test
    void registraPersisteEAssegnaLId() {
        Utente u = gestore.registra("mario", "segreta");

        assertEquals(1, u.getId());
        assertEquals(1, repository.insertCount);
        assertEquals(1, repository.size());
        assertFalse(gestore.isVuoto());
    }

    @Test
    void registraRifiutaUnoUsernameGiaInUso() {
        gestore.registra("mario", "segreta");

        assertThrows(IllegalArgumentException.class, () -> gestore.registra("mario", "altra"));
        assertThrows(IllegalArgumentException.class, () -> gestore.registra("MARIO", "altra"));
        assertEquals(1, repository.size());
    }

    @Test
    void registraRifiutaCredenzialiVuoteSenzaPersistere() {
        assertThrows(IllegalArgumentException.class, () -> gestore.registra("  ", "segreta"));
        assertThrows(IllegalArgumentException.class, () -> gestore.registra("mario", ""));
        assertEquals(0, repository.insertCount);
    }

    @Test
    void autenticaAccettaLeCredenzialiCorrette() {
        gestore.registra("mario", "segreta");

        assertTrue(gestore.autentica("mario", "segreta").isPresent());
        assertTrue(gestore.autentica("MARIO", "segreta").isPresent());
    }

    @Test
    void autenticaRifiutaPasswordSbagliataOUtenteInesistente() {
        gestore.registra("mario", "segreta");

        assertTrue(gestore.autentica("mario", "sbagliata").isEmpty());
        assertTrue(gestore.autentica("luigi", "segreta").isEmpty());
        assertTrue(gestore.autentica(null, "segreta").isEmpty());
    }

    @Test
    void aggiornaPersisteIlCambioDiPassword() {
        Utente u = gestore.registra("mario", "vecchia");

        u.cambiaPassword("nuova");
        gestore.aggiorna(u);

        assertEquals(1, repository.updateCount);
        assertTrue(gestore.autentica("mario", "nuova").isPresent());
        assertTrue(gestore.autentica("mario", "vecchia").isEmpty());
    }

    @Test
    void aggiornaPermetteDiRinominareLUtenteStesso() {
        Utente u = gestore.registra("mario", "segreta");

        u.setUsername("super-mario");
        gestore.aggiorna(u);

        assertTrue(gestore.autentica("super-mario", "segreta").isPresent());
        assertTrue(gestore.autentica("mario", "segreta").isEmpty());
    }

    @Test
    void aggiornaRifiutaUnoUsernameDiUnAltroUtente() {
        gestore.registra("mario", "a");
        Utente luigi = gestore.registra("luigi", "b");

        luigi.setUsername("mario");

        assertThrows(IllegalArgumentException.class, () -> gestore.aggiorna(luigi));
        assertEquals(0, repository.updateCount);
    }

    @Test
    void loadRipopolaDalRepository() {
        gestore.registra("mario", "segreta");

        GestoreUtenti altro = new GestoreUtenti(repository);
        altro.load();

        assertEquals(1, altro.getAll().size());
        assertTrue(altro.autentica("mario", "segreta").isPresent());
    }

    @Test
    void cercaIgnoraMaiuscoleESpazi() {
        gestore.registra("mario", "segreta");

        assertTrue(gestore.cerca("  MaRiO  ").isPresent());
        assertTrue(gestore.cerca("luigi").isEmpty());
    }
}
