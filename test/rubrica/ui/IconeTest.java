package rubrica.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import javax.swing.Icon;
import org.junit.jupiter.api.Test;

/**
 * Le icone sono disegnate a runtime, quindi non c'e' nessun file da ritrovare a
 * classpath: qui si verifica solo che ognuna esista e abbia la misura attesa.
 * Sono {@code BufferedImage}, quindi il test gira anche senza display.
 */
class IconeTest {

    private static final List<Icon> TUTTE = List.of(
            Icone.NUOVO, Icone.MODIFICA, Icone.ELIMINA, Icone.SALVA, Icone.ANNULLA,
            Icone.UTENTE, Icone.NUOVO_UTENTE, Icone.CAMBIA_UTENTE, Icone.LOGIN);

    @Test
    void ogniIconaEQuadrataDelLatoPrevisto() {
        for (Icon icona : TUTTE) {
            assertEquals(Icone.LATO, icona.getIconWidth());
            assertEquals(Icone.LATO, icona.getIconHeight());
        }
    }

    @Test
    void leIconeSonoIstanzeDistinte() {
        assertEquals(TUTTE.size(), TUTTE.stream().distinct().count());
    }
}
