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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import rubrica.domain.Utente;
import rubrica.model.GestoreUtenti;

/**
 * Finestra modale di registrazione di un nuovo utente: username, password e
 * conferma della password, piu' i bottoni Registra e Annulla.
 *
 * <p>La registrazione avviene solo se i due campi password coincidono e se lo
 * username non e' gia' in uso; in caso contrario viene mostrato un messaggio di
 * errore e la finestra resta aperta.</p>
 */
public class RegistrazioneDialog extends JDialog {

    private final GestoreUtenti gestoreUtenti;

    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JPasswordField confermaField = new JPasswordField(16);
    private final JButton registraButton = new JButton("Registra");

    private Utente registrato;

    /**
     * @param owner         la finestra proprietaria (la modale appare sopra di essa)
     * @param gestoreUtenti il model degli utenti su cui registrare
     */
    public RegistrazioneDialog(Window owner, GestoreUtenti gestoreUtenti) {
        super(owner, "Nuovo utente", ModalityType.APPLICATION_MODAL);
        this.gestoreUtenti = gestoreUtenti;

        setContentPane(costruisciContenuto());
        getRootPane().setDefaultButton(registraButton);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel costruisciContenuto() {
        JPanel campi = new JPanel(new GridLayout(3, 2, 8, 8));
        campi.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        campi.add(new JLabel("Utente"));
        campi.add(usernameField);
        campi.add(new JLabel("Password"));
        campi.add(passwordField);
        campi.add(new JLabel("Conferma password"));
        campi.add(confermaField);

        registraButton.addActionListener(e -> onRegistra());
        JButton annulla = new JButton("Annulla");
        annulla.addActionListener(e -> dispose());

        JPanel bottoni = new JPanel();
        bottoni.add(registraButton);
        bottoni.add(annulla);

        JPanel contenuto = new JPanel(new BorderLayout());
        contenuto.add(campi, BorderLayout.CENTER);
        contenuto.add(bottoni, BorderLayout.SOUTH);
        return contenuto;
    }

    private void onRegistra() {
        String password = new String(passwordField.getPassword());
        String conferma = new String(confermaField.getPassword());
        if (!password.equals(conferma)) {
            errore("Le due password non coincidono.");
            passwordField.setText("");
            confermaField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }
        try {
            registrato = gestoreUtenti.registra(usernameField.getText(), password);
            dispose();
        } catch (IllegalArgumentException ex) {
            errore(ex.getMessage());
        } catch (RuntimeException ex) {
            errore("Errore durante il salvataggio dell'utente:\n" + ex.getMessage());
        }
    }

    private void errore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio,
                "Registrazione non riuscita", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra la finestra in modo modale e attende la chiusura.
     *
     * @return l'utente registrato, {@link Optional#empty()} se l'utente ha
     *         annullato o chiuso la finestra
     */
    public Optional<Utente> showDialog() {
        setVisible(true);
        return Optional.ofNullable(registrato);
    }
}
