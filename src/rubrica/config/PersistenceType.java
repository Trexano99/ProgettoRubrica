package rubrica.config;

/**
 * Backend di persistenza selezionabile per la rubrica.
 */
public enum PersistenceType {

    /** Salvataggio su un singolo file di testo ({@code informazioni.txt}). */
    FILE,

    /** Salvataggio su cartella ({@code informazioni}), un file per contatto. */
    DIRECTORY,

    /** Salvataggio su database MySQL (da implementare). */
    MYSQL;

    /**
     * Converte un valore testuale nel tipo corrispondente.
     *
     * @param valore il testo da interpretare (case-insensitive, spazi ignorati);
     *               puo' essere {@code null}
     * @return il tipo riconosciuto, oppure {@link #FILE} se {@code null},
     *         vuoto o non riconosciuto
     */
    public static PersistenceType from(String valore) {
        if (valore == null) {
            return FILE;
        }
        switch (valore.trim().toLowerCase()) {
            case "mysql":
                return MYSQL;
            case "directory":
                return DIRECTORY;
            case "file":
                return FILE;
            default:
                return FILE;
        }
    }
}
