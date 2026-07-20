package rubrica.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordHashTest {

    @Test
    void verificaAccettaLaPasswordCorretta() {
        assertTrue(PasswordHash.of("segreta").verifica("segreta"));
    }

    @Test
    void verificaRifiutaLaPasswordSbagliata() {
        PasswordHash hash = PasswordHash.of("segreta");
        assertFalse(hash.verifica("Segreta"));
        assertFalse(hash.verifica("altro"));
        assertFalse(hash.verifica(""));
        assertFalse(hash.verifica(null));
    }

    @Test
    void stessaPasswordProduceHashDiversiGrazieAlSale() {
        assertNotEquals(PasswordHash.of("segreta").toStorageString(),
                PasswordHash.of("segreta").toStorageString());
    }

    @Test
    void laFormaTestualeNonContieneLaPasswordInChiaro() {
        assertFalse(PasswordHash.of("segreta").toStorageString().contains("segreta"));
    }

    @Test
    void parseRicostruisceUnHashVerificabile() {
        String salvato = PasswordHash.of("segreta").toStorageString();

        PasswordHash ricostruito = PasswordHash.parse(salvato);

        assertTrue(ricostruito.verifica("segreta"));
        assertFalse(ricostruito.verifica("sbagliata"));
        assertEquals(salvato, ricostruito.toStorageString());
    }

    @Test
    void parseRifiutaTestiMalformati() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.parse(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.parse("senzaSeparatore"));
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.parse("a:b:c"));
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.parse("!!!:!!!"));
    }

    @Test
    void toStringNonRivelaSaleNeDigest() {
        PasswordHash hash = PasswordHash.of("segreta");
        assertEquals("PasswordHash{***}", hash.toString());
    }
}
