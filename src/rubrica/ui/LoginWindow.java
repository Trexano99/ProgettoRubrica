package rubrica.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
 * Finestra di login: i campi Utente e Password, il bottone LOGIN e, come extra,
 * un bottone per registrare un nuovo utente.
 *
 * <p>All'avvio dell'applicazione questa e' l'unica finestra mostrata: la
 * finestra principale compare solo dopo un login riuscito. Se le credenziali
 * non sono corrette viene mostrato un messaggio di errore e la finestra resta
 * aperta.</p>
 *
 * <p>Al primo avvio non esiste ancora nessun utente: la finestra lo segnala e
 * invita a registrarne uno.</p>
 */
public class LoginWindow extends JDialog {

    private final GestoreUtenti gestoreUtenti;

    private final JTextField utenteField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JButton loginButton = new JButton("LOGIN");
    private final JLabel messaggio = new JLabel(" ");

    private Utente autenticato;

    /**
     * @param gestoreUtenti il model degli utenti gia' caricato
     */
    public LoginWindow(GestoreUtenti gestoreUtenti) {
        super((java.awt.Frame) null, "Rubrica - Accesso", ModalityType.APPLICATION_MODAL);
        this.gestoreUtenti = gestoreUtenti;

        setContentPane(costruisciContenuto());
        getRootPane().setDefaultButton(loginButton);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (gestoreUtenti.isVuoto()) {
            messaggio.setText("Nessun utente registrato: creane uno.");
        }
    }

    private JPanel costruisciContenuto() {
        JPanel campi = new JPanel(new GridLayout(2, 2, 8, 8));
        campi.add(new JLabel("Utente"));
        campi.add(utenteField);
        campi.add(new JLabel("Password"));
        campi.add(passwordField);

        loginButton.addActionListener(e -> onLogin());
        JButton nuovoUtente = new JButton("Nuovo utente");
        nuovoUtente.addActionListener(e -> onNuovoUtente());

        JPanel bottoni = new JPanel();
        bottoni.add(loginButton);
        bottoni.add(nuovoUtente);

        JPanel contenuto = new JPanel(new BorderLayout(0, 8));
        contenuto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenuto.add(campi, BorderLayout.CENTER);
        contenuto.add(bottoni, BorderLayout.SOUTH);
        contenuto.add(messaggio, BorderLayout.NORTH);
        return contenuto;
    }

    private void onLogin() {
        String password = new String(passwordField.getPassword());
        Optional<Utente> trovato = gestoreUtenti.autentica(utenteField.getText(), password);
        if (trovato.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Login errato: utente o password non corretti.",
                    "Accesso negato", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }
        autenticato = trovato.get();
        dispose();
    }

    private void onNuovoUtente() {
        new RegistrazioneDialog(this, gestoreUtenti).showDialog().ifPresent(u -> {
            utenteField.setText(u.getUsername());
            passwordField.setText("");
            messaggio.setText("Utente '" + u.getUsername() + "' creato: ora accedi.");
            passwordField.requestFocusInWindow();
        });
    }

    /**
     * Mostra la finestra in modo modale e attende la chiusura.
     *
     * @return l'utente autenticato, {@link Optional#empty()} se la finestra e'
     *         stata chiusa senza un login riuscito
     */
    public Optional<Utente> showDialog() {
        setVisible(true);
        return Optional.ofNullable(autenticato);
    }
}
