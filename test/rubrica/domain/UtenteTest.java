package rubrica.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UtenteTest {

    @Test
    void nuovoCreaUtenteSenzaIdEConPasswordVerificabile() {
        Utente u = Utente.nuovo("mario", "segreta");

        assertNull(u.getId());
        assertEquals("mario", u.getUsername());
        assertTrue(u.passwordCorretta("segreta"));
        assertFalse(u.passwordCorretta("sbagliata"));
    }

    @Test
    void nuovoRipulisceGliSpaziDelloUsername() {
        assertEquals("mario", Utente.nuovo("  mario  ", "segreta").getUsername());
    }

    @Test
    void nuovoRifiutaUsernameOPasswordVuoti() {
        assertThrows(IllegalArgumentException.class, () -> Utente.nuovo("", "segreta"));
        assertThrows(IllegalArgumentException.class, () -> Utente.nuovo("   ", "segreta"));
        assertThrows(IllegalArgumentException.class, () -> Utente.nuovo(null, "segreta"));
        assertThrows(IllegalArgumentException.class, () -> Utente.nuovo("mario", ""));
        assertThrows(IllegalArgumentException.class, () -> Utente.nuovo("mario", null));
    }

    @Test
    void cambiaPasswordInvalidaLaPrecedente() {
        Utente u = Utente.nuovo("mario", "vecchia");

        u.cambiaPassword("nuova");

        assertTrue(u.passwordCorretta("nuova"));
        assertFalse(u.passwordCorretta("vecchia"));
    }

    @Test
    void cambiaPasswordRifiutaValoriVuoti() {
        Utente u = Utente.nuovo("mario", "vecchia");
        assertThrows(IllegalArgumentException.class, () -> u.cambiaPassword("  "));
        assertTrue(u.passwordCorretta("vecchia"));
    }

    @Test
    void ugualiSeStessoUsernameAncheConMaiuscoleDiverse() {
        assertEquals(Utente.nuovo("Mario", "a"), Utente.nuovo("mario", "b"));
        assertEquals(Utente.nuovo("Mario", "a").hashCode(), Utente.nuovo("mario", "b").hashCode());
        assertNotEquals(Utente.nuovo("mario", "a"), Utente.nuovo("luigi", "a"));
    }

    @Test
    void toStringNonEsponeLaPassword() {
        assertFalse(Utente.nuovo("mario", "segreta").toString().contains("segreta"));
    }
}
