package rubrica.persistence;

import java.io.File;
import rubrica.config.AppConfig;

/**
 * Sceglie e costruisce l'implementazione di {@link RubricaRepository} in base
 * alla configurazione. Unico punto che conosce le implementazioni concrete:
 * cambiare backend significa cambiare una riga nel file di configurazione.
 */
public final class RepositoryFactory {

    private static final String KEY_FILE_PATH = "persistence.file.path";
    private static final String DEFAULT_FILE_PATH = "informazioni.txt";
    private static final String KEY_DIRECTORY_PATH = "persistence.directory.path";
    private static final String DEFAULT_DIRECTORY_PATH = "informazioni";
    private static final String KEY_UTENTI_PATH = "persistence.utenti.path";
    private static final String DEFAULT_UTENTI_PATH = "utenti.txt";

    private RepositoryFactory() {
    }

    /**
     * Crea il repository corrispondente al {@code persistence.type} configurato.
     *
     * @param config la configurazione dell'applicazione
     * @return il repository da usare
     */
    public static RubricaRepository create(AppConfig config) {
        switch (config.getPersistenceType()) {
            case FILE:
                return new FileRubricaRepository(
                        new File(config.get(KEY_FILE_PATH, DEFAULT_FILE_PATH)));
            case DIRECTORY:
                return new DirectoryRubricaRepository(
                        new File(config.get(KEY_DIRECTORY_PATH, DEFAULT_DIRECTORY_PATH)));
            case MYSQL:
                return new MysqlRubricaRepository(
                        new DriverManagerConnessioni(config.getMysql()));
            default:
                throw new UnsupportedOperationException(
                        "Tipo di persistenza non gestito: " + config.getPersistenceType());
        }
    }

    /**
     * Crea il repository degli utenti corrispondente al {@code persistence.type}
     * configurato. I backend {@code file} e {@code directory} condividono lo
     * stesso storage per gli utenti: un file di testo dedicato
     * ({@code utenti.txt} per default), perche' un file per utente non porterebbe
     * alcun vantaggio.
     *
     * @param config la configurazione dell'applicazione
     * @return il repository degli utenti da usare
     */
    public static UtenteRepository createUtenti(AppConfig config) {
        switch (config.getPersistenceType()) {
            case FILE:
            case DIRECTORY:
                return new FileUtenteRepository(
                        new File(config.get(KEY_UTENTI_PATH, DEFAULT_UTENTI_PATH)));
            case MYSQL:
                return new MysqlUtenteRepository(
                        new DriverManagerConnessioni(config.getMysql()));
            default:
                throw new UnsupportedOperationException(
                        "Tipo di persistenza non gestito: " + config.getPersistenceType());
        }
    }
}
