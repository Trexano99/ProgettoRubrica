package rubrica.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Database H2 in-memory in modalita' MySQL, usato dai test di integrazione dei
 * repository JDBC al posto di un vero server MySQL.
 *
 * <p>{@code MODE=MySQL} fa accettare a H2 la stessa SQL dei repository (fra cui
 * {@code AUTO_INCREMENT}); {@code DB_CLOSE_DELAY=-1} tiene in vita il database
 * finche' la JVM vive, cosi' che le connessioni aperte e chiuse a ogni
 * operazione ritrovino gli stessi dati. Ogni istanza usa un nome casuale, per
 * isolare i test l'uno dall'altro.</p>
 */
class H2Database implements FornitoreConnessioni {

    private final String url;

    H2Database() {
        this.url = "jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
    }

    @Override
    public Connection apri() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    /** Crea le tabelle {@code persone} e {@code utenti} vuote. */
    void creaSchema() {
        esegui(
                "CREATE TABLE utenti ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "username VARCHAR(255) NOT NULL UNIQUE,"
                        + "password_hash VARCHAR(255) NOT NULL)",
                "CREATE TABLE persone ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY,"
                        + "nome VARCHAR(255) NOT NULL,"
                        + "cognome VARCHAR(255) NOT NULL,"
                        + "indirizzo VARCHAR(255),"
                        + "telefono VARCHAR(255) NOT NULL,"
                        + "eta INT NOT NULL)");
    }

    private void esegui(String... comandi) {
        try (Connection conn = apri();
             Statement st = conn.createStatement()) {
            for (String comando : comandi) {
                st.execute(comando);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Setup H2 fallito", e);
        }
    }
}
