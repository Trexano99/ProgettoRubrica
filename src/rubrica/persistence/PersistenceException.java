package rubrica.persistence;

/**
 * Errore non recuperabile dello strato di persistenza.
 *
 * <p>Wrappa le eccezioni tecniche del backend (in primis {@link java.sql.SQLException})
 * in un'eccezione unchecked, cosi' che i contratti dei repository restino puliti
 * e uguali per tutti i backend. Il resto dell'applicazione la intercetta dove
 * mostra gli errori all'utente, senza dipendere dai dettagli di JDBC.</p>
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param messaggio la descrizione dell'operazione fallita
     * @param causa     l'eccezione tecnica originale
     */
    public PersistenceException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
