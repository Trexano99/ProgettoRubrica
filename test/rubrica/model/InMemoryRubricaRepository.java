package rubrica.model;

import java.util.ArrayList;
import java.util.List;
import rubrica.domain.Persona;
import rubrica.persistence.RubricaRepository;

/**
 * Repository in memoria per i test del model. Replica il contratto senza
 * I/O: {@code insert} assegna id crescenti, {@code update}/{@code delete}
 * individuano per id.
 */
class InMemoryRubricaRepository implements RubricaRepository {

    private final List<Persona> store = new ArrayList<>();
    private int counter = 0;

    int insertCount = 0;
    int updateCount = 0;
    int deleteCount = 0;

    @Override
    public List<Persona> loadAll() {
        return new ArrayList<>(store);
    }

    @Override
    public void insert(Persona p) {
        insertCount++;
        p.setId(++counter);
        store.add(p);
    }

    @Override
    public void update(Persona p) {
        updateCount++;
        store.set(indexOfId(p.getId()), p);
    }

    @Override
    public void delete(Persona p) {
        deleteCount++;
        store.remove(indexOfId(p.getId()));
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
