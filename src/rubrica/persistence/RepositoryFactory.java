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

    private RepositoryFactory() {
    }

    /**
     * Crea il repository corrispondente al {@code persistence.type} configurato.
     *
     * @param config la configurazione dell'applicazione
     * @return il repository da usare
     * @throws UnsupportedOperationException se il tipo configurato non e' ancora
     *         implementato (es. {@code mysql})
     */
    public static RubricaRepository create(AppConfig config) {
        switch (config.getPersistenceType()) {
            case FILE:
                return new FileRubricaRepository(
                        new File(config.get(KEY_FILE_PATH, DEFAULT_FILE_PATH)));
            case MYSQL:
                throw new UnsupportedOperationException(
                        "Persistenza MySQL non ancora implementata");
            default:
                throw new UnsupportedOperationException(
                        "Tipo di persistenza non gestito: " + config.getPersistenceType());
        }
    }
}
