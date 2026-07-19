package rubrica.ui;

import javax.swing.table.AbstractTableModel;
import rubrica.domain.Persona;
import rubrica.model.Rubrica;

/**
 * Adatta la {@link Rubrica} a una {@link javax.swing.JTable}, mostrando solo le
 * colonne Nome, Cognome e Telefono. La sorgente dei dati
 * resta la {@code Rubrica}; dopo ogni modifica il chiamante invoca
 * {@link #refresh()}.
 */
public class PersonaTableModel extends AbstractTableModel {

    private static final String[] COLONNE = {"Nome", "Cognome", "Telefono"};

    private final Rubrica rubrica;

    /**
     * @param rubrica il model da esporre nella tabella
     */
    public PersonaTableModel(Rubrica rubrica) {
        this.rubrica = rubrica;
    }

    @Override
    public int getRowCount() {
        return rubrica.size();
    }

    @Override
    public int getColumnCount() {
        return COLONNE.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLONNE[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Persona p = rubrica.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return p.getNome();
            case 1:
                return p.getCognome();
            case 2:
                return p.getTelefono();
            default:
                throw new IndexOutOfBoundsException("Colonna inesistente: " + columnIndex);
        }
    }

    /**
     * @param rowIndex la riga selezionata nella tabella
     * @return il contatto corrispondente
     */
    public Persona getPersonaAt(int rowIndex) {
        return rubrica.get(rowIndex);
    }

    /**
     * Notifica alla tabella che i dati sottostanti sono cambiati (dopo
     * inserimento, modifica o eliminazione).
     */
    public void refresh() {
        fireTableDataChanged();
    }
}
