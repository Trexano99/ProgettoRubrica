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
import rubrica.domain.PasswordHash;
import rubrica.domain.Utente;
import rubrica.model.GestoreUtenti;

/**
 * Finestra modale di modifica dell'utenza collegata: permette di cambiare lo
 * username e, se si vuole, la password.
 *
 * <p>Per confermare qualunque modifica va digitata la password attuale: senza,
 * chiunque trovasse la sessione aperta potrebbe cambiare le credenziali. I due
 * campi della nuova password possono restare vuoti, e in quel caso la password
 * resta invariata.</p>
 *
 * <p>L'utente non viene modificato sul posto: si costruisce un nuovo
 * {@link Utente} con gli stessi {@code id} e lo si passa a
 * {@link GestoreUtenti#aggiorna(Utente)}, cosi' che un errore di salvataggio non
 * lasci in memoria dati gia' cambiati.</p>
 */
public class ProfiloUtenteDialog extends JDialog {

    private final GestoreUtenti gestoreUtenti;
    private final Utente corrente;

    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField attualeField = new JPasswordField(16);
    private final JPasswordField nuovaField = new JPasswordField(16);
    private final JPasswordField confermaField = new JPasswordField(16);

    private Utente aggiornato;

    /**
     * @param owner         la finestra proprietaria (la modale appare sopra di essa)
     * @param gestoreUtenti il model degli utenti su cui salvare
     * @param corrente      l'utente collegato, di cui modificare i dati
     */
    public ProfiloUtenteDialog(Window owner, GestoreUtenti gestoreUtenti, Utente corrente) {
        super(owner, "Utenza - " + corrente.getUsername(), ModalityType.APPLICATION_MODAL);
        this.gestoreUtenti = gestoreUtenti;
        this.corrente = corrente;

        usernameField.setText(corrente.getUsername());
        setContentPane(costruisciContenuto());
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel costruisciContenuto() {
        JPanel campi = new JPanel(new GridLayout(4, 2, 8, 8));
        campi.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        campi.add(new JLabel("Utente"));
        campi.add(usernameField);
        campi.add(new JLabel("Password attuale"));
        campi.add(attualeField);
        campi.add(new JLabel("Nuova password"));
        campi.add(nuovaField);
        campi.add(new JLabel("Conferma nuova password"));
        campi.add(confermaField);

        JButton salva = new JButton("Salva");
        salva.addActionListener(e -> onSalva());
        JButton annulla = new JButton("Annulla");
        annulla.addActionListener(e -> dispose());

        JPanel bottoni = new JPanel();
        bottoni.add(salva);
        bottoni.add(annulla);

        JLabel nota = new JLabel("Lascia vuota la nuova password per non cambiarla.");
        nota.setBorder(BorderFactory.createEmptyBorder(0, 12, 4, 12));

        JPanel basso = new JPanel(new BorderLayout());
        basso.add(nota, BorderLayout.NORTH);
        basso.add(bottoni, BorderLayout.SOUTH);

        JPanel contenuto = new JPanel(new BorderLayout());
        contenuto.add(campi, BorderLayout.CENTER);
        contenuto.add(basso, BorderLayout.SOUTH);
        return contenuto;
    }

    private void onSalva() {
        if (!corrente.passwordCorretta(new String(attualeField.getPassword()))) {
            errore("La password attuale non e' corretta.");
            attualeField.setText("");
            attualeField.requestFocusInWindow();
            return;
        }

        String nuova = new String(nuovaField.getPassword());
        String conferma = new String(confermaField.getPassword());
        if (!nuova.equals(conferma)) {
            errore("Le due nuove password non coincidono.");
            nuovaField.setText("");
            confermaField.setText("");
            nuovaField.requestFocusInWindow();
            return;
        }

        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            errore("Lo username e' obbligatorio.");
            return;
        }

        PasswordHash hash = nuova.isEmpty()
                ? corrente.getPasswordHash()
                : PasswordHash.of(nuova);
        Utente modificato = new Utente(corrente.getId(), username.trim(), hash);
        try {
            gestoreUtenti.aggiorna(modificato);
            aggiornato = modificato;
            dispose();
        } catch (IllegalArgumentException ex) {
            errore(ex.getMessage());
        } catch (RuntimeException ex) {
            errore("Errore durante il salvataggio dell'utenza:\n" + ex.getMessage());
        }
    }

    private void errore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio,
                "Modifica non riuscita", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra la finestra in modo modale e attende la chiusura.
     *
     * @return l'utente con i dati aggiornati se il salvataggio e' andato a buon
     *         fine, {@link Optional#empty()} se l'utente ha annullato
     */
    public Optional<Utente> showDialog() {
        setVisible(true);
        return Optional.ofNullable(aggiornato);
    }
}
