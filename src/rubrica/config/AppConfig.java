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
 *   <li>{@code persistence.type} = {@code file} | {@code directory} | {@code mysql}
 *       (default {@code file})</li>
 *   <li>{@code persistence.mysql.host} (default {@code localhost})</li>
 *   <li>{@code persistence.mysql.port} (default {@code 3306})</li>
 *   <li>{@code persistence.mysql.database} (default {@code rubrica})</li>
 *   <li>{@code persistence.mysql.user} (default {@code root})</li>
 *   <li>{@code persistence.mysql.password} (default vuota)</li>
 * </ul>
 */
public class AppConfig {

    /** Nome di default del file di configurazione, cercato nella working dir. */
    public static final String DEFAULT_FILE = "rubrica.properties";

    private static final String KEY_PERSISTENCE_TYPE = "persistence.type";

    private static final String KEY_MYSQL_HOST = "persistence.mysql.host";
    private static final String KEY_MYSQL_PORT = "persistence.mysql.port";
    private static final String KEY_MYSQL_DATABASE = "persistence.mysql.database";
    private static final String KEY_MYSQL_USER = "persistence.mysql.user";
    private static final String KEY_MYSQL_PASSWORD = "persistence.mysql.password";

    private static final String DEFAULT_MYSQL_HOST = "localhost";
    private static final int DEFAULT_MYSQL_PORT = 3306;
    private static final String DEFAULT_MYSQL_DATABASE = "rubrica";
    private static final String DEFAULT_MYSQL_USER = "root";
    private static final String DEFAULT_MYSQL_PASSWORD = "";

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
     * @return i parametri di connessione a MySQL, con i default applicati alle
     *         chiavi assenti. Una porta non numerica ricade sul default.
     */
    public ImpostazioniMysql getMysql() {
        return new ImpostazioniMysql(
                properties.getProperty(KEY_MYSQL_HOST, DEFAULT_MYSQL_HOST),
                intOrDefault(properties.getProperty(KEY_MYSQL_PORT), DEFAULT_MYSQL_PORT),
                properties.getProperty(KEY_MYSQL_DATABASE, DEFAULT_MYSQL_DATABASE),
                properties.getProperty(KEY_MYSQL_USER, DEFAULT_MYSQL_USER),
                properties.getProperty(KEY_MYSQL_PASSWORD, DEFAULT_MYSQL_PASSWORD));
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

    private static int intOrDefault(String valore, int valoreDefault) {
        if (valore == null) {
            return valoreDefault;
        }
        try {
            return Integer.parseInt(valore.trim());
        } catch (NumberFormatException e) {
            System.err.println("Porta MySQL non valida (" + valore + "), uso il default " + valoreDefault);
            return valoreDefault;
        }
    }
}
