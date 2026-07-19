package rubrica.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import rubrica.domain.Persona;
import rubrica.model.Rubrica;

/**
 * Finestra principale dell'applicazione: una {@link JTable} con un contatto per
 * riga (Nome, Cognome, Telefono) e i tre bottoni Nuovo, Modifica, Elimina, con
 * i flussi previsti dai requisiti.
 */
public class MainWindow extends JFrame {

    private final Rubrica rubrica;
    private final PersonaTableModel tableModel;
    private final JTable tabella;

    /**
     * @param rubrica il model gia' caricato da mostrare
     */
    public MainWindow(Rubrica rubrica) {
        super("Rubrica");
        this.rubrica = rubrica;
        this.tableModel = new PersonaTableModel(rubrica);
        this.tabella = new JTable(tableModel);
        this.tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setContentPane(costruisciContenuto());
        setPreferredSize(new Dimension(520, 360));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel costruisciContenuto() {
        JButton nuovo = new JButton("Nuovo");
        nuovo.addActionListener(e -> onNuovo());
        JButton modifica = new JButton("Modifica");
        modifica.addActionListener(e -> onModifica());
        JButton elimina = new JButton("Elimina");
        elimina.addActionListener(e -> onElimina());

        JPanel bottoni = new JPanel();
        bottoni.add(nuovo);
        bottoni.add(modifica);
        bottoni.add(elimina);

        JPanel contenuto = new JPanel(new BorderLayout());
        contenuto.add(new JScrollPane(tabella), BorderLayout.CENTER);
        contenuto.add(bottoni, BorderLayout.SOUTH);
        return contenuto;
    }

    private void onNuovo() {
        new PersonaEditor(this, null).showDialog().ifPresent(p ->
                esegui(() -> {
                    rubrica.add(p);
                    tableModel.refresh();
                }));
    }

    private void onModifica() {
        Persona selezionata = personaSelezionata(
                "Per modificare, seleziona prima una persona.");
        if (selezionata == null) {
            return;
        }
        new PersonaEditor(this, selezionata).showDialog().ifPresent(p ->
                esegui(() -> {
                    rubrica.update(p);
                    tableModel.refresh();
                }));
    }

    private void onElimina() {
        Persona selezionata = personaSelezionata(
                "Per eliminare, seleziona prima una persona.");
        if (selezionata == null) {
            return;
        }
        int scelta = JOptionPane.showConfirmDialog(this,
                "Eliminare la persona " + selezionata.getNome() + " "
                        + selezionata.getCognome() + "?",
                "Conferma eliminazione", JOptionPane.YES_NO_OPTION);
        if (scelta == JOptionPane.YES_OPTION) {
            esegui(() -> {
                rubrica.delete(selezionata);
                tableModel.refresh();
            });
        }
    }

    /**
     * @param messaggioSeAssente messaggio mostrato se nessuna riga e' selezionata
     * @return la persona selezionata, oppure {@code null} (mostrando l'errore)
     */
    private Persona personaSelezionata(String messaggioSeAssente) {
        int riga = tabella.getSelectedRow();
        if (riga < 0) {
            JOptionPane.showMessageDialog(this, messaggioSeAssente,
                    "Nessuna selezione", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return tableModel.getPersonaAt(riga);
    }

    /** Esegue un'operazione di persistenza mostrando un dialog in caso di errore. */
    private void esegui(Runnable operazione) {
        try {
            operazione.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante il salvataggio dei dati:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
