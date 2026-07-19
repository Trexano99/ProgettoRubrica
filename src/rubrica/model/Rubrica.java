package rubrica.model;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import rubrica.domain.Persona;
import rubrica.persistence.RubricaRepository;

/**
 * Model dell'applicazione: mantiene in memoria l'elenco dei contatti e delega
 * ogni operazione al {@link RubricaRepository}.
 *
 * <p>Ogni azione (add/update/delete) esegue una singola operazione mirata sul
 * backend, mantenendo allineata la lista in memoria.</p>
 */
public class Rubrica {

    private final RubricaRepository repository;
    private final List<Persona> contatti = new Vector<>();

    /**
     * @param repository lo strato di persistenza da usare
     */
    public Rubrica(RubricaRepository repository) {
        this.repository = repository;
    }

    /**
     * Carica (o ricarica) tutti i contatti dal repository, rimpiazzando lo stato
     * in memoria. Da invocare all'avvio.
     */
    public void load() {
        contatti.clear();
        contatti.addAll(repository.loadAll());
    }

    /**
     * Aggiunge un nuovo contatto: gli assegna l'id e lo inserisce
     * nella lista.
     *
     * @param p il contatto da aggiungere
     * @throws IllegalArgumentException se il telefono e' assente o vuoto
     */
    public void add(Persona p) {
        richiediTelefono(p);
        repository.insert(p);
        contatti.add(p);
    }

    /**
     * Aggiorna un contatto esistente, individuato per id, sia nel repository sia
     * nella lista.
     *
     * @param p il contatto con i dati aggiornati (id valorizzato)
     * @throws IllegalArgumentException se il telefono e' assente o vuoto
     */
    public void update(Persona p) {
        richiediTelefono(p);
        repository.update(p);
        contatti.set(indexOfId(p.getId()), p);
    }

    private static void richiediTelefono(Persona p) {
        if (p.getTelefono() == null || p.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("Il telefono e' obbligatorio.");
        }
    }

    /**
     * Elimina un contatto, individuato per id, sia dal repository sia dalla lista.
     *
     * @param p il contatto da eliminare
     */
    public void delete(Persona p) {
        repository.delete(p);
        contatti.remove(indexOfId(p.getId()));
    }

    /**
     * @return vista non modificabile dei contatti correnti
     */
    public List<Persona> getAll() {
        return Collections.unmodifiableList(contatti);
    }

    /**
     * @param index posizione nella lista
     * @return il contatto in quella posizione
     */
    public Persona get(int index) {
        return contatti.get(index);
    }

    /**
     * @return il numero di contatti in rubrica
     */
    public int size() {
        return contatti.size();
    }

    private int indexOfId(Integer id) {
        for (int i = 0; i < contatti.size(); i++) 
            if (contatti.get(i).getId().equals(id)) 
                return i;
        throw new IllegalArgumentException("Contatto non trovato, id=" + id);
    }
}
