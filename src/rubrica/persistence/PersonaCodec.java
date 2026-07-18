package rubrica.persistence;

import rubrica.domain.Persona;

/**
 * Conversione fra una {@link Persona} e la sua riga di testo nei file.
 *
 * <p>Formato riga:
 * {@code nome;cognome;indirizzo;telefono;eta}</p>
 */
public final class PersonaCodec {

    private static final String SEPARATORE = ";";
    private static final int NUMERO_CAMPI = 5;

    private PersonaCodec() {
    }

    /**
     * Serializza la persona in una riga. L'id surrogato non viene incluso.
     *
     * @param p la persona da serializzare
     * @return la riga nel formato {@code nome;cognome;indirizzo;telefono;eta}
     */
    public static String toLine(Persona p) {
        return String.join(SEPARATORE,
                p.getNome(),
                p.getCognome(),
                p.getIndirizzo(),
                p.getTelefono(),
                Integer.toString(p.getEta()));
    }

    /**
     * Ricostruisce una persona da una riga. L'id resta {@code null}.
     *
     * @param riga la riga nel formato {@code nome;cognome;indirizzo;telefono;eta}
     * @return la persona ricostruita, con id {@code null}
     * @throws IllegalArgumentException se il numero di campi e' errato o
     *         l'eta' non e' un intero
     */
    public static Persona fromLine(String riga) {
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length != NUMERO_CAMPI) {
            throw new IllegalArgumentException(
                    "Riga malformata, attesi " + NUMERO_CAMPI + " campi: " + riga);
        }
        int eta;
        try {
            eta = Integer.parseInt(campi[4].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Eta' non intera nella riga: " + riga, e);
        }
        return new Persona(campi[0], campi[1], campi[2], campi[3], eta);
    }
}
