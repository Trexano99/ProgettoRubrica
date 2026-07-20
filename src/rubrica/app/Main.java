package rubrica.app;

import java.util.Optional;
import javax.swing.SwingUtilities;
import rubrica.config.AppConfig;
import rubrica.domain.Utente;
import rubrica.model.GestoreUtenti;
import rubrica.model.Rubrica;
import rubrica.persistence.RepositoryFactory;
import rubrica.ui.LoginWindow;
import rubrica.ui.MainWindow;

/**
 * Main dell'applicazione Rubrica.
 * Assembla i componenti  (configurazione, persistenza, model,
 * interfaccia) e avvia l'applicazione dalla finestra di login.
 *
 * <p>La finestra principale viene costruita e mostrata solo dopo un accesso
 * riuscito; se l'utente chiude il login senza autenticarsi, l'applicazione
 * termina senza aprire nulla.</p>
 */
public final class Main {

    private Main() {
    }

    /**
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        AppConfig config = AppConfig.load();

        GestoreUtenti gestoreUtenti = new GestoreUtenti(RepositoryFactory.createUtenti(config));
        gestoreUtenti.load();

        SwingUtilities.invokeLater(() -> {
            Optional<Utente> utente = new LoginWindow(gestoreUtenti).showDialog();
            if (utente.isEmpty()) 
                System.exit(0);
            Rubrica rubrica = new Rubrica(RepositoryFactory.create(config));
            rubrica.load();
            new MainWindow(rubrica, utente.get()).setVisible(true);
        });
    }
}
