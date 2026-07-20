package rubrica.domain;

import java.util.Objects;

/**
 * Utente che accede alla rubrica.
 *
 * <p>La password non viene mai conservata in chiaro: l'utente porta con se' il
 * solo {@link PasswordHash}, cioe' salt e digest. La verifica avviene con
 * {@link #passwordCorretta(String)}.</p>
 *
 * <p>Come per {@link Persona}, l'{@code id} e' una chiave surrogata gestita
 * dallo strato di persistenza ({@code null} finche' l'utente non e' stato
 * salvato) ed e' escluso da {@link #equals(Object)}/{@link #hashCode()}.</p>
 */
public class Utente {

    private Integer id;
    private String username;
    private PasswordHash passwordHash;

    /**
     * Crea un utente a partire dalla password in chiaro, che viene subito
     * trasformata in hash con un salt casuale.
     *
     * @param username         il nome utente (non vuoto)
     * @param passwordInChiaro la password scelta (non vuota)
     * @return il nuovo utente, con {@code id} ancora {@code null}
     * @throws IllegalArgumentException se username o password sono vuoti
     */
    public static Utente nuovo(String username, String passwordInChiaro) {
        richiediNonVuoto(username, "Lo username e' obbligatorio.");
        richiediNonVuoto(passwordInChiaro, "La password e' obbligatoria.");
        return new Utente(null, username.trim(), PasswordHash.of(passwordInChiaro));
    }

    /**
     * @param id           la chiave surrogata ({@code null} se non salvato)
     * @param username     il nome utente
     * @param passwordHash salt e digest della password
     */
    public Utente(Integer id, String username, PasswordHash passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        richiediNonVuoto(username, "Lo username e' obbligatorio.");
        this.username = username.trim();
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sostituisce la password, ricalcolando salt e digest.
     *
     * @param passwordInChiaro la nuova password (non vuota)
     * @throws IllegalArgumentException se la password e' vuota
     */
    public void cambiaPassword(String passwordInChiaro) {
        richiediNonVuoto(passwordInChiaro, "La password e' obbligatoria.");
        this.passwordHash = PasswordHash.of(passwordInChiaro);
    }

    /**
     * @param passwordInChiaro la password digitata al login
     * @return {@code true} se corrisponde a quella dell'utente
     */
    public boolean passwordCorretta(String passwordInChiaro) {
        return passwordHash != null && passwordHash.verifica(passwordInChiaro);
    }

    private static void richiediNonVuoto(String valore, String messaggio) {
        if (valore == null || valore.trim().isEmpty()) 
            throw new IllegalArgumentException(messaggio);
    }

    /** Uguaglianza sullo username, confrontato senza distinzione di maiuscole. */
    @Override
    public boolean equals(Object o) {
        if (this == o) 
            return true;
        if (o == null || getClass() != o.getClass()) 
            return false;
        Utente utente = (Utente) o;
        return username != null
                ? username.equalsIgnoreCase(utente.username)
                : utente.username == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username == null ? null : username.toLowerCase());
    }

    /** Rappresentazione priva di qualunque dato sulla password. */
    @Override
    public String toString() {
        return "Utente{id=" + id + ", username=" + username + '}';
    }
}
