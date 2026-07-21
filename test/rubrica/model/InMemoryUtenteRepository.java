package rubrica.model;

import java.util.ArrayList;
import java.util.List;
import rubrica.domain.Utente;
import rubrica.persistence.UtenteRepository;

/**
 * Repository degli utenti in memoria per i test del model. Replica il contratto
 * senza I/O: {@code insert} assegna id crescenti, {@code update} individua per id.
 */
class InMemoryUtenteRepository implements UtenteRepository {

    private final List<Utente> store = new ArrayList<>();
    private int counter = 0;

    int insertCount = 0;
    int updateCount = 0;

    @Override
    public List<Utente> loadAll() {
        return new ArrayList<>(store);
    }

    @Override
    public void insert(Utente u) {
        insertCount++;
        u.setId(++counter);
        store.add(u);
    }

    @Override
    public void update(Utente u) {
        updateCount++;
        store.set(indexOfId(u.getId()), u);
    }

    int size() {
        return store.size();
    }

    private int indexOfId(Integer id) {
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equals(id)) {
                return i;
            }
        }
        throw new IllegalArgumentException("id assente: " + id);
    }
}
