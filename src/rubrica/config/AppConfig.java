package rubrica.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configurazione dell'applicazione, letta da un file {@code .properties}
 * opzionale. Fornisce, fra anche il {@link PersistenceType} con cui
 * {@code RepositoryFactory} sceglie il backend di persistenza.
 *
 * <p>Chiavi riconosciute:</p>
 * <ul>
 *   <li>{@code persistence.type} = {@code file} | {@code mysql} (default {@code file})</li>
 * </ul>
 */
public class AppConfig {

    /** Nome di default del file di configurazione, cercato nella working dir. */
    public static final String DEFAULT_FILE = "rubrica.properties";

    private static final String KEY_PERSISTENCE_TYPE = "persistence.type";

    private final Properties properties;

    /**
     * @param properties le proprieta' gia' caricate (mai {@code null})
     */
    public AppConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Carica la configurazione dal file di default nella working dir. Se il file
     * non esiste, restituisce una configurazione vuota (tutti i default).
     *
     * @return la configurazione caricata
     */
    public static AppConfig load() {
        return load(new File(DEFAULT_FILE));
    }

    /**
     * Carica la configurazione dal file indicato. Se il file non esiste,
     * restituisce una configurazione vuota (tutti i default).
     *
     * @param file il file {@code .properties} da leggere
     * @return la configurazione caricata
     */
    public static AppConfig load(File file) {
        Properties props = new Properties();
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Configurazione non leggibile, uso i default: " + e.getMessage());
            }
        }
        return new AppConfig(props);
    }

    /**
     * @return il backend di persistenza configurato, {@link PersistenceType#FILE}
     *         se non specificato o non riconosciuto
     */
    public PersistenceType getPersistenceType() {
        return PersistenceType.from(properties.getProperty(KEY_PERSISTENCE_TYPE));
    }

    /**
     * Accesso generico a una proprieta' (utile ai backend futuri, es. MySQL).
     *
     * @param chiave       la chiave da leggere
     * @param valoreDefault il valore restituito se la chiave e' assente
     * @return il valore configurato o {@code valoreDefault}
     */
    public String get(String chiave, String valoreDefault) {
        return properties.getProperty(chiave, valoreDefault);
    }
}
