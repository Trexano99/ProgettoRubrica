package rubrica.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import rubrica.domain.Persona;

/**
 * Finestra modale di inserimento/modifica di una {@link Persona}: una riga
 * {@code JLabel + JTextField} per ogni dato, piu' i bottoni Salva e Annulla.
 *
 * <p>Uso tipico:</p>
 * <pre>{@code
 * PersonaEditor editor = new PersonaEditor(owner, personaOppureNull);
 * editor.showDialog().ifPresent(p -> ...);
 * }</pre>
 *
 * <p>In modifica, l'{@code id} della persona originale viene preservato.</p>
 */
public class PersonaEditor extends JDialog {

    private final JTextField nomeField = new JTextField(20);
    private final JTextField cognomeField = new JTextField(20);
    private final JTextField indirizzoField = new JTextField(20);
    private final JTextField telefonoField = new JTextField(20);
    private final JTextField etaField = new JTextField(20);

    private final Integer idOriginale;
    private Persona risultato;

    /**
     * @param owner   la finestra proprietaria (la modale appare sopra di essa)
     * @param persona la persona da modificare, oppure {@code null} per una nuova
     */
    public PersonaEditor(Window owner, Persona persona) {
        super(owner, persona == null ? "Nuova persona" : "Modifica persona",
                ModalityType.APPLICATION_MODAL);

        this.idOriginale = persona == null ? null : persona.getId();
        if (persona != null) {
            nomeField.setText(persona.getNome());
            cognomeField.setText(persona.getCognome());
            indirizzoField.setText(persona.getIndirizzo());
            telefonoField.setText(persona.getTelefono());
            etaField.setText(Integer.toString(persona.getEta()));
        }

        setContentPane(costruisciContenuto());
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel costruisciContenuto() {
        JPanel campi = new JPanel(new GridLayout(5, 2, 8, 8));
        campi.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        campi.add(new JLabel("Nome"));
        campi.add(nomeField);
        campi.add(new JLabel("Cognome"));
        campi.add(cognomeField);
        campi.add(new JLabel("Indirizzo"));
        campi.add(indirizzoField);
        campi.add(new JLabel("Telefono"));
        campi.add(telefonoField);
        campi.add(new JLabel("Eta"));
        campi.add(etaField);

        JButton salva = new JButton("Salva");
        salva.addActionListener(e -> onSalva());
        JButton annulla = new JButton("Annulla");
        annulla.addActionListener(e -> onAnnulla());

        JPanel bottoni = new JPanel();
        bottoni.add(salva);
        bottoni.add(annulla);

        JPanel contenuto = new JPanel(new BorderLayout());
        contenuto.add(campi, BorderLayout.CENTER);
        contenuto.add(bottoni, BorderLayout.SOUTH);
        return contenuto;
    }

    private void onSalva() {
        int eta;
        try {
            eta = Integer.parseInt(etaField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "L'eta deve essere un numero intero.",
                    "Dato non valido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (telefonoField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Il telefono e' obbligatorio.",
                    "Dato mancante", JOptionPane.ERROR_MESSAGE);
            return;
        }
        risultato = new Persona(idOriginale,
                nomeField.getText(),
                cognomeField.getText(),
                indirizzoField.getText(),
                telefonoField.getText(),
                eta);
        dispose();
    }

    private void onAnnulla() {
        risultato = null;
        dispose();
    }

    /**
     * Mostra la finestra in modo modale e attende la chiusura.
     *
     * @return la persona inserita/modificata se l'utente ha premuto Salva,
     *         {@link Optional#empty()} se ha annullato o chiuso la finestra
     */
    public Optional<Persona> showDialog() {
        setVisible(true);
        return Optional.ofNullable(risultato);
    }
}
