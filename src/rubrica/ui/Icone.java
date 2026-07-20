package rubrica.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Le piccole icone dei bottoni delle barre degli strumenti.
 *
 * <p>Sono disegnate a runtime con Java2D invece di essere caricate da file
 * immagine: cosi' il jar resta autosufficiente, non ci sono risorse da
 * ritrovare a classpath e le icone restano nitide su qualunque tema.</p>
 *
 * <p>Ogni icona e' un quadrato di {@link #LATO} pixel, costruita una sola volta
 * e riusata da tutte le finestre.</p>
 */
public final class Icone {

    /** Lato in pixel di ogni icona. */
    public static final int LATO = 16;

    private static final Color VERDE  = new Color(0x2E, 0x7D, 0x32);
    private static final Color BLU    = new Color(0x15, 0x65, 0xC0);
    private static final Color ROSSO  = new Color(0xC6, 0x28, 0x28);
    private static final Color AMBRA  = new Color(0xEF, 0x6C, 0x00);
    private static final Color GRIGIO = new Color(0x54, 0x6E, 0x7A);

    /** Contatto nuovo: una croce verde. */
    public static final Icon NUOVO = disegna(g -> {
        g.setColor(VERDE);
        g.fillRect(6, 2, 4, 12);
        g.fillRect(2, 6, 12, 4);
    });

    /** Modifica: una matita in diagonale. */
    public static final Icon MODIFICA = disegna(g -> {
        g.setColor(AMBRA);
        g.setStroke(new BasicStroke(4f));
        g.drawLine(6, 10, 12, 4);
        g.setColor(GRIGIO);
        g.fillPolygon(new int[] {2, 6, 4}, new int[] {14, 12, 10}, 3);
    });

    /** Eliminazione: un cestino. */
    public static final Icon ELIMINA = disegna(g -> {
        g.setColor(ROSSO);
        g.fillRect(6, 1, 4, 2);
        g.fillRect(2, 3, 12, 2);
        g.fillRect(4, 6, 8, 9);
        g.setColor(Color.WHITE);
        g.fillRect(6, 8, 1, 5);
        g.fillRect(9, 8, 1, 5);
    });

    /** Salvataggio: un dischetto. */
    public static final Icon SALVA = disegna(g -> {
        g.setColor(BLU);
        g.fillRect(1, 1, 14, 14);
        g.setColor(Color.WHITE);
        g.fillRect(5, 2, 6, 5);
        g.fillRect(4, 9, 8, 5);
        g.setColor(BLU);
        g.fillRect(8, 3, 2, 3);
    });

    /** Annullamento: una croce obliqua. */
    public static final Icon ANNULLA = disegna(g -> {
        g.setColor(GRIGIO);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(3, 3, 12, 12);
        g.drawLine(12, 3, 3, 12);
    });

    /** Utenza: la sagoma di una persona. */
    public static final Icon UTENTE = disegna(g -> {
        g.setColor(BLU);
        g.fillOval(5, 1, 6, 6);
        g.fillArc(2, 8, 12, 12, 0, 180);
    });

    /** Nuova utenza: la sagoma di una persona con una croce. */
    public static final Icon NUOVO_UTENTE = disegna(g -> {
        g.setColor(BLU);
        g.fillOval(3, 1, 6, 6);
        g.fillArc(0, 8, 12, 12, 0, 180);
        g.setColor(VERDE);
        g.fillRect(12, 8, 3, 7);
        g.fillRect(10, 10, 7, 3);
    });

    /** Cambio utente: due frecce contrapposte. */
    public static final Icon CAMBIA_UTENTE = disegna(g -> {
        g.setColor(GRIGIO);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(2, 5, 12, 5);
        g.fillPolygon(new int[] {11, 15, 11}, new int[] {2, 5, 8}, 3);
        g.drawLine(14, 11, 4, 11);
        g.fillPolygon(new int[] {5, 1, 5}, new int[] {8, 11, 14}, 3);
    });

    /** Accesso: una freccia che entra in una porta. */
    public static final Icon LOGIN = disegna(g -> {
        g.setColor(GRIGIO);
        g.fillRect(12, 1, 3, 14);
        g.setColor(VERDE);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(1, 8, 8, 8);
        g.fillPolygon(new int[] {7, 12, 7}, new int[] {3, 8, 13}, 3);
    });

    private Icone() {
    }

    /**
     * Costruisce un'icona quadrata trasparente delegando il disegno al pittore.
     *
     * @param pittore il codice che disegna sulla tela, gia' con l'antialiasing
     *                attivo e l'origine nell'angolo in alto a sinistra
     * @return l'icona pronta per un bottone
     */
    private static Icon disegna(Consumer<Graphics2D> pittore) {
        BufferedImage tela = new BufferedImage(LATO, LATO, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tela.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        pittore.accept(g);
        g.dispose();
        return new ImageIcon(tela);
    }
}
