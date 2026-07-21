package rubrica.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Password di un {@link Utente} conservata come salt + digest.
 *
 * <p>Il digest e' {@code SHA-256} applicato a {@code salt + password} e
 * ripetuto {@link #ITERAZIONI} volte. Il salt e' casuale e diverso per ogni 
 * password, cosi' che due utenti con la stessa password abbiano digest diversi.</p>
 *
 * <p>La forma testuale usata per salvare l'utenza è {@code salt:digest}, con
 * entrambe le parti in Base64.</p>
 */
public final class PasswordHash {

    /** Algoritmo di digest, presente in ogni JDK. */
    private static final String ALGORITMO = "SHA-256";

    /** Ripetizioni del digest */
    private static final int ITERAZIONI = 10_000;

    /** Lunghezza in byte del salt casuale. */
    private static final int BYTE_SALT = 16;

    private static final String SEPARATORE = ":";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final byte[] salt;
    private final byte[] digest;

    private PasswordHash(byte[] salt, byte[] digest) {
        this.salt = salt;
        this.digest = digest;
    }

    /**
     * Calcola l'hash di una password con un salt casuale appena generato.
     *
     * @param passwordInChiaro la password da proteggere
     * @return l'hash corrispondente
     */
    public static PasswordHash of(String passwordInChiaro) {
        byte[] salt = new byte[BYTE_SALT];
        RANDOM.nextBytes(salt);
        return new PasswordHash(salt, calcola(salt, passwordInChiaro));
    }

    /**
     * Ricostruisce un hash gia' calcolato dalla sua forma testuale.
     *
     * @param testo la stringa {@code salt:digest} in Base64
     * @return l'hash ricostruito
     * @throws IllegalArgumentException se il testo non ha il formato atteso
     */
    public static PasswordHash parse(String testo) {
        if (testo == null) {
            throw new IllegalArgumentException("Hash della password assente.");
        }
        String[] parti = testo.split(SEPARATORE, -1);
        if (parti.length != 2) {
            throw new IllegalArgumentException("Hash della password malformato: " + testo);
        }
        try {
            return new PasswordHash(
                    Base64.getDecoder().decode(parti[0]),
                    Base64.getDecoder().decode(parti[1]));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Hash della password malformato: " + testo, e);
        }
    }

    /**
     * @param passwordInChiaro la password da verificare
     * @return {@code true} se produce lo stesso digest con lo stesso salt
     */
    public boolean verifica(String passwordInChiaro) {
        if (passwordInChiaro == null) {
            return false;
        }
        return MessageDigest.isEqual(digest, calcola(salt, passwordInChiaro));
    }

    /**
     * @return la forma testuale {@code salt:digest} da salvare
     */
    public String toStorageString() {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + SEPARATORE + encoder.encodeToString(digest);
    }

    private static byte[] calcola(byte[] salt, String passwordInChiaro) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITMO);
            md.update(salt);
            byte[] corrente = md.digest(passwordInChiaro.getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i < ITERAZIONI; i++) {
                md.reset();
                md.update(salt);
                corrente = md.digest(corrente);
            }
            return corrente;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo " + ALGORITMO + " non disponibile", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordHash altro = (PasswordHash) o;
        return MessageDigest.isEqual(salt, altro.salt)
                && MessageDigest.isEqual(digest, altro.digest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(digest));
    }

    /** Non espone ne' salt ne' digest: evita di stamparli per sbaglio nei log. */
    @Override
    public String toString() {
        return "PasswordHash{***}";
    }
}
