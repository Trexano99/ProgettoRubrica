package rubrica.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import rubrica.domain.Utente;
import rubrica.persistence.UtenteRepository;

/**
 * Model degli utenti: mantiene in memoria l'elenco degli account e delega ogni
 * operazione all'{@link UtenteRepository}.
 *
 * <p>Si occupa dell'autenticazione al login e della registrazione di nuovi
 * utenti, garantendo che lo username sia unico (confronto senza distinzione di
 * maiuscole).</p>
 */
public class GestoreUtenti {

    private final UtenteRepository repository;
    private final List<Utente> utenti = new Vector<>();

    /**
     * @param repository lo strato di persistenza da usare
     */
    public GestoreUtenti(UtenteRepository repository) {
        this.repository = repository;
    }

    /**
     * Carica (o ricarica) tutti gli utenti dal repository. Da invocare all'avvio.
     */
    public void load() {
        utenti.clear();
        utenti.addAll(repository.loadAll());
    }

    /**
     * Verifica le credenziali di login.
     *
     * @param username lo username digitato
     * @param password la password digitata
     * @return l'utente autenticato, {@link Optional#empty()} se lo username non
     *         esiste o la password non corrisponde
     */
    public Optional<Utente> autentica(String username, String password) {
        return cerca(username).filter(u -> u.passwordCorretta(password));
    }

    /**
     * Registra un nuovo utente e lo persiste.
     *
     * @param username lo username scelto (non vuoto, non gia' in uso)
     * @param password la password scelta (non vuota)
     * @return l'utente creato, con {@code id} valorizzato
     * @throws IllegalArgumentException se username o password sono vuoti, o se
     *         lo username e' gia' in uso
     */
    public Utente registra(String username, String password) {
        Utente nuovo = Utente.nuovo(username, password);
        if (cerca(nuovo.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "Lo username '" + nuovo.getUsername() + "' e' gia' in uso.");
        }
        repository.insert(nuovo);
        utenti.add(nuovo);
        return nuovo;
    }

    /**
     * Persiste le modifiche a un utente esistente (username o password).
     *
     * @param u l'utente da aggiornare (id valorizzato)
     * @throws IllegalArgumentException se l'utente non esiste o se il nuovo
     *         username e' gia' di un altro utente
     */
    public void aggiorna(Utente u) {
        int index = indexOfId(u.getId());
        Optional<Utente> omonimo = cerca(u.getUsername());
        if (omonimo.isPresent() && !omonimo.get().getId().equals(u.getId())) {
            throw new IllegalArgumentException(
                    "Lo username '" + u.getUsername() + "' e' gia' in uso.");
        }
        repository.update(u);
        utenti.set(index, u);
    }

    /**
     * @param username lo username da cercare (confronto senza distinzione di
     *                 maiuscole, spazi ignorati)
     * @return l'utente corrispondente, se esiste
     */
    public Optional<Utente> cerca(String username) {
        if (username == null) {
            return Optional.empty();
        }
        String cercato = username.trim();
        return utenti.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(cercato))
                .findFirst();
    }

    /**
     * @return {@code true} se non esiste ancora nessun utente registrato: al
     *         primo avvio la finestra di login propone la registrazione
     */
    public boolean isVuoto() {
        return utenti.isEmpty();
    }

    /**
     * @return vista non modificabile degli utenti registrati
     */
    public List<Utente> getAll() {
        return Collections.unmodifiableList(utenti);
    }

    private int indexOfId(Integer id) {
        for (int i = 0; i < utenti.size(); i++)
            if (utenti.get(i).getId().equals(id))
                return i;
        throw new IllegalArgumentException("Utente non trovato, id=" + id);
    }
}
