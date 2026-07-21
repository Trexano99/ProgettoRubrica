package rubrica.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rubrica.domain.Persona;

/**
 * Persistenza della rubrica su database, tramite JDBC.
 *
 * <p>Sostituisce i backend su file:
 * {@code persone} (colonne {@code id}, {@code nome}, {@code cognome},
 * {@code indirizzo}, {@code telefono}, {@code eta}). L'{@code id} surrogato di
 * {@link Persona} e' la chiave primaria auto-incrementante del database, letta
 * dalle chiavi generate all'insert; a differenza dei backend su file, dove
 * dipende dalla posizione di riga, qui resta stabile fra un avvio e l'altro.</p>
 */
public class MysqlRubricaRepository implements RubricaRepository {

    private static final String SELECT_ALL =
            "SELECT id, nome, cognome, indirizzo, telefono, eta FROM persone ORDER BY id";
    private static final String INSERT =
            "INSERT INTO persone (nome, cognome, indirizzo, telefono, eta) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE persone SET nome = ?, cognome = ?, indirizzo = ?, telefono = ?, eta = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM persone WHERE id = ?";

    private final FornitoreConnessioni connessioni;

    /**
     * @param connessioni la sorgente delle connessioni JDBC
     */
    public MysqlRubricaRepository(FornitoreConnessioni connessioni) {
        this.connessioni = connessioni;
    }

    @Override
    public List<Persona> loadAll() {
        List<Persona> risultato = new ArrayList<>();
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                risultato.add(new Persona(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("indirizzo"),
                        rs.getString("telefono"),
                        rs.getInt("eta")));
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nel caricamento dei contatti", e);
        }
        return risultato;
    }

    @Override
    public void insert(Persona p) {
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            valorizza(ps, p);
            ps.executeUpdate();
            try (ResultSet chiavi = ps.getGeneratedKeys()) {
                if (chiavi.next()) {
                    p.setId(chiavi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nell'inserimento del contatto", e);
        }
    }

    @Override
    public void update(Persona p) {
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            valorizza(ps, p);
            ps.setInt(6, p.getId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nell'aggiornamento del contatto", e);
        }
    }

    @Override
    public void delete(Persona p) {
        try (Connection conn = connessioni.apri();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, p.getId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Contatto non trovato, id=" + p.getId());
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore nell'eliminazione del contatto", e);
        }
    }

    /** Riempie i primi cinque parametri con i dati del contatto. */
    private static void valorizza(PreparedStatement ps, Persona p) throws SQLException {
        ps.setString(1, p.getNome());
        ps.setString(2, p.getCognome());
        ps.setString(3, p.getIndirizzo());
        ps.setString(4, p.getTelefono());
        ps.setInt(5, p.getEta());
    }
}
