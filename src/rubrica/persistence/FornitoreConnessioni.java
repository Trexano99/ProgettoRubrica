package rubrica.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fornisce connessioni JDBC ai repository su database.
 *
 * <p>E' un piccolo strato di indirezione fra i repository e il modo in cui la
 * connessione viene aperta: in produzione punta a MySQL via
 * {@link DriverManagerConnessioni}, nei test a un database in-memory. Cosi' i
 * repository restano identici nei due contesti e sono verificabili senza un
 * MySQL in ascolto.</p>
 */
@FunctionalInterface
public interface FornitoreConnessioni {

    /**
     * Apre una nuova connessione. Il chiamante e' responsabile di chiuderla
     * (tipicamente con un try-with-resources).
     *
     * @return una connessione pronta all'uso
     * @throws SQLException se la connessione non puo' essere aperta
     */
    Connection apri() throws SQLException;
}
