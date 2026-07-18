package rubrica.persistence;

import java.util.List;
import rubrica.domain.Persona;

/**
 * Astrazione di persistenza della rubrica.
 *
 * <p>Ogni implementazione decide come materializzare l'{@code id} surrogato di
 * {@link Persona}: su file per posizione di riga, su database come chiave
 * primaria. Il resto dell'applicazione dipende solo da questa interfaccia,
 * cosi' che il backend di persistenza sia intercambiabile.</p>
 */
public interface RubricaRepository {

    /**
     * Carica tutti i contatti persistiti. L'ordine e' significativo: a ciascun
     * contatto viene assegnato un {@code id} coerente con l'implementazione.
     *
     * @return la lista dei contatti; lista vuota se non esiste nulla di
     *         persistito (mai {@code null})
     */
    List<Persona> loadAll();

    /**
     * Persiste un nuovo contatto assegnandogli un {@code id}.
     *
     * @param p il contatto da inserire; al ritorno il suo {@code id} e'
     *          valorizzato
     */
    void insert(Persona p);

    /**
     * Aggiorna un contatto esistente, individuato dal suo {@code id}.
     *
     * @param p il contatto con i dati aggiornati e l'{@code id} da individuare
     */
    void update(Persona p);

    /**
     * Elimina un contatto esistente, individuato dal suo {@code id}.
     *
     * @param p il contatto da eliminare (rileva il solo {@code id})
     */
    void delete(Persona p);
}
