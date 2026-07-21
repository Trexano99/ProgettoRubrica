package rubrica.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configurazione dell'applicazione, letta da due file {@code .properties}
 * opzionali. Fornisce, fra anche il {@link PersistenceType} con cui
 * {@code RepositoryFactory} sceglie il backend di persistenza.
 *
 * <p>La configurazione si compone di due file, entrambi opzionali, cercati nella
 * working dir:</p>
 * <ul>
 *   <li>{@code rubrica.properties}: scelta del backend e percorsi dei file dati;</li>
 *   <li>{@code credenziali_database.properties}: credenziali del server MySQL,
 *       il file che l'utente finale sostituisce durante l'installazione. Le sue
 *       chiavi hanno la precedenza in caso di collisione.</li>
 * </ul>
 *
 * <p>Chiavi riconosciute:</p>
 * <ul>
 *   <li>{@code persistence.type} = {@code file} | {@code directory} | {@code mysql}
 *       (default {@code file})</li>
 *   <li>{@code db.host} — ip del server MySQL (default {@code localhost})</li>
 *   <li>{@code db.port} — porta (default {@code 3306})</li>
 *   <li>{@code db.database} — nome del database (default {@code rubrica})</li>
 *   <li>{@code db.user} — username (default {@code root})</li>
 *   <li>{@code db.password} — password (default vuota)</li>
 * </ul>
 */
public class AppConfig {

    /** Nome di default del file di configurazione, cercato nella working dir. */
    public static final String DEFAULT_FILE = "rubrica.properties";

    /**
     * Nome di default del file con le credenziali MySQL, cercato nella working
     * dir. E' il file che l'utente finale modifica con i parametri del proprio
     * sistema; le sue chiavi vincono su quelle di {@link #DEFAULT_FILE}.
     */
    public static final String DEFAULT_CREDENZIALI_FILE = "credenziali_database.properties";

    private static final String KEY_PERSISTENCE_TYPE = "persistence.type";

    private static final String KEY_MYSQL_HOST = "db.host";
    private static final String KEY_MYSQL_PORT = "db.port";
    private static final String KEY_MYSQL_DATABASE = "db.database";
    private static final String KEY_MYSQL_USER = "db.user";
    private static final String KEY_MYSQL_PASSWORD = "db.password";

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
     * Carica la configurazione dai file di default nella working dir
     * ({@link #DEFAULT_FILE} piu' {@link #DEFAULT_CREDENZIALI_FILE}). I file
     * assenti vengono semplicemente ignorati (valgono i default); le chiavi del
     * file delle credenziali hanno la precedenza.
     *
     * @return la configurazione caricata
     */
    public static AppConfig load() {
        return load(new File(DEFAULT_FILE), new File(DEFAULT_CREDENZIALI_FILE));
    }

    /**
     * Carica la configurazione dal solo file indicato. Se il file non esiste,
     * restituisce una configurazione vuota (tutti i default).
     *
     * @param file il file {@code .properties} da leggere
     * @return la configurazione caricata
     */
    public static AppConfig load(File file) {
        return load(file, null);
    }

    /**
     * Carica la configurazione unendo due file: prima {@code config}, poi
     * {@code credenziali}, cosi' che le credenziali sovrascrivano eventuali
     * chiavi omonime. Ogni file {@code null} o inesistente viene ignorato.
     *
     * @param config      il file con backend e percorsi (puo' essere {@code null})
     * @param credenziali il file con le credenziali MySQL (puo' essere {@code null})
     * @return la configurazione caricata
     */
    public static AppConfig load(File config, File credenziali) {
        Properties props = new Properties();
        caricaSePresente(props, config);
        caricaSePresente(props, credenziali);
        return new AppConfig(props);
    }

    /** Fonde nel {@code props} le chiavi del file, se esiste; altrimenti nulla. */
    private static void caricaSePresente(Properties props, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("Configurazione non leggibile, uso i default: " + e.getMessage());
        }
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
