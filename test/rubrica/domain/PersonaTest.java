package rubrica.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PersonaTest {

    @Test
    void costruttoreEGetterConservanoIDati() {
        Persona p = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);

        assertNull(p.getId());
        assertEquals("Steve", p.getNome());
        assertEquals("Jobs", p.getCognome());
        assertEquals("via Cupertino 13", p.getIndirizzo());
        assertEquals("0612344", p.getTelefono());
        assertEquals(56, p.getEta());
    }

    @Test
    void idEValorizzabile() {
        Persona p = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        p.setId(7);
        assertEquals(7, p.getId());
    }

    @Test
    void uguaglianzaSuiCampiDiDominioIgnorandoLId() {
        Persona a = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        Persona b = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        a.setId(1);
        b.setId(2);

        assertEquals(a, b, "stessi dati di dominio => uguali anche con id diverso");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void datiDiversiRendonoDiverse() {
        Persona a = new Persona("Steve", "Jobs", "via Cupertino 13", "0612344", 56);
        Persona b = new Persona("Bill", "Gates", "via Redmond 10", "06688989", 60);
        assertNotEquals(a, b);
    }
}
