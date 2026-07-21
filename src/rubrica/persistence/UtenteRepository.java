package rubrica.persistence;

import java.util.List;
import rubrica.domain.Utente;

/**
 * Astrazione di persistenza degli utenti che accedono al software.
 *
 * <p>Speculare a {@link RubricaRepository}: il resto dell'applicazione dipende
 * solo da questa interfaccia, cosi' che il backend sia intercambiabile (file
 * oggi, database domani).</p>
 */
public interface UtenteRepository {

    /**
     * Carica tutti gli utenti persistiti, assegnando a ciascuno un {@code id}
     * coerente con l'implementazione.
     *
     * @return la lista degli utenti; vuota se non esiste nulla di persistito
     *         (mai {@code null})
     */
    List<Utente> loadAll();

    /**
     * Persiste un nuovo utente assegnandogli un {@code id}.
     *
     * @param u l'utente da inserire; al ritorno il suo {@code id} e' valorizzato
     */
    void insert(Utente u);

    /**
     * Aggiorna un utente esistente, individuato dal suo {@code id}.
     *
     * @param u l'utente con i dati aggiornati e l'{@code id} da individuare
     */
    void update(Utente u);
}
