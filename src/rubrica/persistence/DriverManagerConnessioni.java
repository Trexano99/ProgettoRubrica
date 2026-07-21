package rubrica.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import rubrica.config.ImpostazioniMysql;

/**
 * {@link FornitoreConnessioni} che apre connessioni verso MySQL tramite
 * {@link DriverManager}, con i parametri presi da {@link ImpostazioniMysql}.
 *
 * <p>Non serve registrare esplicitamente il driver: dal JDBC 4 il connector/J,
 * se presente a classpath, si registra da solo. Il jar del connector va quindi
 * aggiunto solo a runtime, non serve per compilare.</p>
 */
public class DriverManagerConnessioni implements FornitoreConnessioni {

    private final String url;
    private final String user;
    private final String password;

    /**
     * @param impostazioni i parametri di connessione al database
     */
    public DriverManagerConnessioni(ImpostazioniMysql impostazioni) {
        this.url = impostazioni.jdbcUrl();
        this.user = impostazioni.getUser();
        this.password = impostazioni.getPassword();
    }

    @Override
    public Connection apri() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
