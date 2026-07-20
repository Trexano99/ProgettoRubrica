package rubrica.config;

/**
 * I parametri di connessione al database MySQL, ricavati dalla configurazione.
 *
 * <p>La classe si limita a custodire i valori e a comporre l'URL JDBC: non apre
 * connessioni, cosi' e' verificabile senza un database in ascolto.</p>
 */
public final class ImpostazioniMysql {

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    /**
     * @param host     l'indirizzo del server MySQL
     * @param port     la porta di ascolto
     * @param database il nome del database da usare
     * @param user     l'utente di accesso
     * @param password la password di accesso
     */
    public ImpostazioniMysql(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Compone l'URL JDBC verso il database.
     *
     * <p>{@code serverTimezone=UTC} evita l'errore che alcune versioni del
     * driver sollevano quando il fuso del server non e' noto;
     * {@code useSSL=false} silenzia l'avviso su una connessione locale non
     * cifrata, tipica di un MySQL di sviluppo; {@code allowPublicKeyRetrieval=true}
     * serve al plugin {@code caching_sha2_password} (default da MySQL 8) per
     * scambiare la chiave RSA quando la connessione non e' cifrata, altrimenti
     * l'handshake fallisce con "Public Key Retrieval is not allowed".</p>
     *
     * @return l'URL nel formato {@code jdbc:mysql://host:port/database?...}
     */
    public String jdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    }

    /** Non espone la password: evita di stamparla per sbaglio nei log. */
    @Override
    public String toString() {
        return "ImpostazioniMysql{host=" + host + ", port=" + port
                + ", database=" + database + ", user=" + user + "}";
    }
}
