package rubrica.persistence;

import rubrica.domain.PasswordHash;
import rubrica.domain.Utente;

/**
 * Conversione fra un {@link Utente} e la sua riga di testo nel file degli utenti.
 *
 * <p>Formato riga: {@code username;salt:digest}. La password non compare mai in
 * chiaro: il secondo campo e' la forma testuale di {@link PasswordHash}.</p>
 */
public final class UtenteCodec {

    private static final String SEPARATORE = ";";
    private static final int NUMERO_CAMPI = 2;

    private UtenteCodec() {
    }

    /**
     * Serializza l'utente in una riga. L'id surrogato non viene incluso.
     *
     * @param u l'utente da serializzare
     * @return la riga nel formato {@code username;salt:digest}
     */
    public static String toLine(Utente u) {
        return u.getUsername() + SEPARATORE + u.getPasswordHash().toStorageString();
    }

    /**
     * Ricostruisce un utente da una riga. L'id resta {@code null}.
     *
     * @param riga la riga nel formato {@code username;salt:digest}
     * @return l'utente ricostruito, con id {@code null}
     * @throws IllegalArgumentException se il numero di campi e' errato, lo
     *         username e' vuoto o l'hash e' malformato
     */
    public static Utente fromLine(String riga) {
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length != NUMERO_CAMPI) {
            throw new IllegalArgumentException(
                    "Riga malformata, attesi " + NUMERO_CAMPI + " campi: " + riga);
        }
        if (campi[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Username assente nella riga: " + riga);
        }
        return new Utente(null, campi[0].trim(), PasswordHash.parse(campi[1]));
    }
}
