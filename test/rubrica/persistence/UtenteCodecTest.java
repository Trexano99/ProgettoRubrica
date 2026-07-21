package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rubrica.domain.Utente;

class UtenteCodecTest {

    @Test
    void toLineNonContieneLaPasswordInChiaro() {
        String riga = UtenteCodec.toLine(Utente.nuovo("mario", "segreta"));

        assertTrue(riga.startsWith("mario;"));
        assertFalse(riga.contains("segreta"));
    }

    @Test
    void andataERitornoConservaUsernameEPassword() {
        Utente originale = Utente.nuovo("mario", "segreta");

        Utente ricostruito = UtenteCodec.fromLine(UtenteCodec.toLine(originale));

        assertEquals("mario", ricostruito.getUsername());
        assertNull(ricostruito.getId());
        assertTrue(ricostruito.passwordCorretta("segreta"));
        assertFalse(ricostruito.passwordCorretta("sbagliata"));
    }

    @Test
    void fromLineRifiutaRigheMalformate() {
        assertThrows(IllegalArgumentException.class, () -> UtenteCodec.fromLine("soloUsername"));
        assertThrows(IllegalArgumentException.class, () -> UtenteCodec.fromLine("a;b;c"));
        assertThrows(IllegalArgumentException.class, () -> UtenteCodec.fromLine(";sale:digest"));
        assertThrows(IllegalArgumentException.class, () -> UtenteCodec.fromLine("mario;hashRotto"));
    }
}
