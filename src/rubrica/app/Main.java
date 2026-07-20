package rubrica.app;

import javax.swing.SwingUtilities;
import rubrica.config.AppConfig;
import rubrica.model.Rubrica;
import rubrica.persistence.RepositoryFactory;
import rubrica.persistence.RubricaRepository;
import rubrica.ui.MainWindow;

/**
 * Main dell'applicazione Rubrica. 
 * Assembla i componenti  (configurazione, persistenza, model,
 * interfaccia) e avvia la finestra principale caricando 
 * i dati persistiti.
 */
public final class Main {

    private Main() {
    }

    /**
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        AppConfig config = AppConfig.load();
        RubricaRepository repository = RepositoryFactory.create(config);
        Rubrica rubrica = new Rubrica(repository);
        rubrica.load();

        SwingUtilities.invokeLater(() -> new MainWindow(rubrica).setVisible(true));
    }
}
