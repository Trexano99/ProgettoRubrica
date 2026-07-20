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
 *
 * <p>Il bottone Cambia utente della finestra principale riporta al login
 * riavviando la stessa sequenza: la rubrica viene ricaricata da capo, cosi' che
 * il nuovo utente non erediti nulla dalla sessione precedente.</p>
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

        SwingUtilities.invokeLater(() -> avviaSessione(config, gestoreUtenti));
    }

    /**
     * Mostra il login e, se l'accesso riesce, apre la finestra principale.
     *
     * @param config        la configurazione da cui ricavare la persistenza
     * @param gestoreUtenti il model degli utenti, condiviso fra le sessioni
     */
    private static void avviaSessione(AppConfig config, GestoreUtenti gestoreUtenti) {
        Optional<Utente> utente = new LoginWindow(gestoreUtenti).showDialog();
        if (utente.isEmpty())
            System.exit(0);
        Rubrica rubrica = new Rubrica(RepositoryFactory.create(config));
        rubrica.load();
        MainWindow finestra = new MainWindow(rubrica, gestoreUtenti, utente.get());
        // invokeLater e non una chiamata diretta: la sessione successiva parte
        // dopo che questa finestra si e' davvero chiusa, senza annidare login.
        finestra.setOnCambiaUtente(() ->
                SwingUtilities.invokeLater(() -> avviaSessione(config, gestoreUtenti)));
        finestra.setVisible(true);
    }
}
