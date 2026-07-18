package rubrica.domain;

import java.util.Objects;

/**
 * Contatto della rubrica.
 *
 * <p>L'{@code id} e' una chiave surrogata gestita dallo strato di persistenza
 * ({@code null} finche' la persona non e' stata salvata). Non e' un dato di
 * dominio: e' escluso da {@link #equals(Object)}/{@link #hashCode()} e non viene
 * scritto in maniera persitente.</p>
 */
public class Persona {

    private Integer id;
    private String nome;
    private String cognome;
    private String indirizzo;
    private String telefono;
    private int eta;

    public Persona(String nome, String cognome, String indirizzo, String telefono, int eta) {
        this(null, nome, cognome, indirizzo, telefono, eta);
    }

    public Persona(Integer id, String nome, String cognome, String indirizzo, String telefono, int eta) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.eta = eta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    /** Uguaglianza sui soli dati di dominio; l'id surrogato e' ignorato. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Persona persona = (Persona) o;
        return eta == persona.eta
                && Objects.equals(nome, persona.nome)
                && Objects.equals(cognome, persona.cognome)
                && Objects.equals(indirizzo, persona.indirizzo)
                && Objects.equals(telefono, persona.telefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, indirizzo, telefono, eta);
    }

    @Override
    public String toString() {
        return "Persona{id=" + id + ", nome=" + nome + ", cognome=" + cognome
                + ", indirizzo=" + indirizzo + ", telefono=" + telefono + ", eta=" + eta + '}';
    }
}
