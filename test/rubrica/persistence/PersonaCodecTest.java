package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import rubrica.domain.Persona;

class PersonaCodecTest {

    @Test
    void toLineProduceCampiSeparatiDaPuntoEVirgolaSenzaId() {
        Persona p = new Persona(42, "Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        assertEquals("Steve;Jobs;via Cupertino 13;0612344;56", PersonaCodec.toLine(p));
    }

    @Test
    void fromLineRicostruisceLaPersonaConIdNullo() {
        Persona p = PersonaCodec.fromLine("Steve;Jobs;via Cupertino 13;0612344;56");
        assertNull(p.getId());
        assertEquals("Steve", p.getNome());
        assertEquals("Jobs", p.getCognome());
        assertEquals("via Cupertino 13", p.getIndirizzo());
        assertEquals("0612344", p.getTelefono());
        assertEquals(56, p.getEta());
    }

    @Test
    void roundTripPreservaIDatiDiDominio() {
        Persona originale = new Persona("Babbo", "Natale", "via del Polo Nord", "00000111", 99);
        Persona ricostruita = PersonaCodec.fromLine(PersonaCodec.toLine(originale));
        assertEquals(originale, ricostruita);
    }

    @Test
    void gestisceLeTreRigheDEsempioDelRequisito() {
        assertEquals(new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56),
                PersonaCodec.fromLine("Steve;Jobs;via Cupertino 13;0612344;56"));
        assertEquals(new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60),
                PersonaCodec.fromLine("Bill;Gates;via Redmond 10;06688989;60"));
        assertEquals(new Persona("Babbo", "Natale", "via del Polo Nord", "00000111", 99),
                PersonaCodec.fromLine("Babbo;Natale;via del Polo Nord;00000111;99"));
    }

    @Test
    void rigaConNumeroCampiErratoLanciaEccezione() {
        assertThrows(IllegalArgumentException.class,
                () -> PersonaCodec.fromLine("Steve;Jobs;via Cupertino 13"));
    }

    @Test
    void etaNonInteraLanciaEccezione() {
        assertThrows(IllegalArgumentException.class,
                () -> PersonaCodec.fromLine("Steve;Jobs;via Cupertino 13;0612344;cinquantasei"));
    }
}
