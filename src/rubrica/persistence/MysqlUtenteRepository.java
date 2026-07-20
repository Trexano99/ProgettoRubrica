package rubrica.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rubrica.domain.PasswordHash;
import rubrica.domain.Utente;

/**
 * Persistenza degli utenti su database, tramite JDBC.
 *
 * <p>Speculare a {@link FileUtenteRepository} ma con lo storage su una tabella
 * {@code utenti} (colonne {@code id}, {@code username}, {@code password_hash}).
 * A differenza dei backend su file, l'{@code id} non e' la posizione di riga ma
 * la chiave primaria auto-incrementante del database, letta dalle chiavi
 * generate all'insert.</p>
 *
 * <p>La password non viene mai salvata in chiaro: la colonna {@code password_hash}
 * contiene la forma testuale {@code salt:digest} di {@link PasswordHash}, la
 * stessa usata dai backend su file.</p>
 */
public class MysqlUtenteRepository implements UtenteRepository {

    private static final String SELECT_ALL =
            "SELECT id, username, password_hash FROM utenti ORDER BY id";
    private static final String INSERT =
            "INSERT INTO utenti (username, password_hash) VALUES (?, ?)";
    private static final String UPDATE =
            "UPDATE utenti SET username = ?, password_hash = ? WHERE id = ?";

    private final FornitoreConnessioni connessioni;

    /**
     * @param connessioni la sorgente delle connessioni JDBC
     */
    public MysqlUtenteRepository(FornitoreConnessioni connessioni) {
        this.connessioni = connessioni;
    }

    @Override
    public List<Utente> loadAll() {
        List<Utente> risultato = new ArrayList<>();
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                risultato.add(new Utente(
                        rs.getInt("id"),
                        rs.getString("username"),
                        PasswordHash.parse(rs.getString("password_hash"))));
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nel caricamento degli utenti", e);
        }
        return risultato;
    }

    @Override
    public void insert(Utente u) {
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash().toStorageString());
            ps.executeUpdate();
            try (ResultSet chiavi = ps.getGeneratedKeys()) {
                if (chiavi.next()) {
                    u.setId(chiavi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nell'inserimento dell'utente", e);
        }
    }

    @Override
    public void update(Utente u) {
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash().toStorageString());
            ps.setInt(3, u.getId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Utente non trovato, id=" + u.getId());
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nell'aggiornamento dell'utente", e);
        }
    }
}
