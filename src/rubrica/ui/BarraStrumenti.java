package rubrica.ui;

import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

/**
 * La barra degli strumenti usata da tutte le finestre dell'applicazione: al
 * posto dei bottoni in basso, una {@link JToolBar} in alto con un bottone per
 * ogni azione, icona sopra e testo sotto.
 *
 * <p>La barra non e' trascinabile ({@code setFloatable(false)}): i bottoni sono
 * pochi e una barra staccabile confonderebbe soltanto.</p>
 */
public class BarraStrumenti extends JToolBar {

    /** Crea una barra vuota, orizzontale e fissa. */
    public BarraStrumenti() {
        setFloatable(false);
        setRollover(true);
    }

    /**
     * Aggiunge un bottone in coda alla barra.
     *
     * @param testo    l'etichetta sotto l'icona
     * @param icona    l'icona da mostrare (vedi {@link Icone})
     * @param tooltip  il suggerimento mostrato al passaggio del mouse
     * @param azione   il codice da eseguire al click
     * @return il bottone creato, per poterlo eventualmente configurare ancora
     */
    public JButton aggiungi(String testo, Icon icona, String tooltip, ActionListener azione) {
        JButton bottone = new JButton(testo, icona);
        bottone.setVerticalTextPosition(SwingConstants.BOTTOM);
        bottone.setHorizontalTextPosition(SwingConstants.CENTER);
        bottone.setToolTipText(tooltip);
        bottone.setFocusable(false);
        bottone.addActionListener(azione);
        add(bottone);
        return bottone;
    }
}
