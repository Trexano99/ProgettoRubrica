package rubrica.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import rubrica.domain.Persona;
import rubrica.domain.Utente;
import rubrica.model.GestoreUtenti;
import rubrica.model.Rubrica;

/**
 * Finestra principale dell'applicazione: una {@link JTable} con un contatto per
 * riga (Nome, Cognome, Telefono) e, nella barra degli strumenti in alto, le
 * azioni Nuovo, Modifica ed Elimina con i flussi previsti dai requisiti.
 *
 * <p>La stessa barra ospita le due azioni sull'utenza: la modifica dei dati
 * dell'utente collegato e il cambio utente, che chiude la finestra e riporta al
 * login tramite la callback registrata con
 * {@link #setOnCambiaUtente(Runnable)}.</p>
 */
public class MainWindow extends JFrame {

    private final Rubrica rubrica;
    private final GestoreUtenti gestoreUtenti;
    private final PersonaTableModel tableModel;
    private final JTable tabella;

    private Utente utente;
    private Runnable onCambiaUtente;

    /**
     * @param rubrica       il model gia' caricato da mostrare
     * @param gestoreUtenti il model degli utenti, per la modifica dell'utenza;
     *                      {@code null} se l'applicazione gira senza autenticazione
     * @param utente        l'utente che ha effettuato l'accesso, mostrato nel
     *                      titolo; {@code null} se non c'e' autenticazione
     */
    public MainWindow(Rubrica rubrica, GestoreUtenti gestoreUtenti, Utente utente) {
        this.rubrica = rubrica;
        this.gestoreUtenti = gestoreUtenti;
        this.utente = utente;
        this.tableModel = new PersonaTableModel(rubrica);
        this.tabella = new JTable(tableModel);
        this.tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        aggiornaTitolo();
        setContentPane(costruisciContenuto());
        setPreferredSize(new Dimension(560, 380));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Registra il codice da eseguire quando si preme Cambia utente, dopo che
     * questa finestra si e' chiusa: tipicamente riapre la finestra di login.
     *
     * @param azione il codice da eseguire, {@code null} per disabilitare il
     *               cambio utente
     */
    public void setOnCambiaUtente(Runnable azione) {
        this.onCambiaUtente = azione;
    }

    private JPanel costruisciContenuto() {
        BarraStrumenti barra = new BarraStrumenti();
        barra.aggiungi("Nuovo", Icone.NUOVO,
                "Crea un nuovo contatto", e -> onNuovo());
        barra.aggiungi("Modifica", Icone.MODIFICA,
                "Modifica il contatto selezionato", e -> onModifica());
        barra.aggiungi("Elimina", Icone.ELIMINA,
                "Elimina il contatto selezionato", e -> onElimina());
        if (utente != null && gestoreUtenti != null) {
            barra.addSeparator();
            barra.aggiungi("Utenza", Icone.UTENTE,
                    "Modifica username e password dell'utente collegato",
                    e -> onUtenza());
            barra.aggiungi("Cambia utente", Icone.CAMBIA_UTENTE,
                    "Chiudi la sessione e torna alla finestra di login",
                    e -> onCambiaUtente());
        }

        JPanel contenuto = new JPanel(new BorderLayout());
        contenuto.add(barra, BorderLayout.NORTH);
        contenuto.add(new JScrollPane(tabella), BorderLayout.CENTER);
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

    private void onUtenza() {
        new ProfiloUtenteDialog(this, gestoreUtenti, utente).showDialog().ifPresent(u -> {
            utente = u;
            aggiornaTitolo();
            JOptionPane.showMessageDialog(this, "Utenza aggiornata.",
                    "Utenza", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void onCambiaUtente() {
        if (onCambiaUtente == null) {
            return;
        }
        int scelta = JOptionPane.showConfirmDialog(this,
                "Chiudere la sessione di " + utente.getUsername()
                        + " e tornare al login?",
                "Cambia utente", JOptionPane.YES_NO_OPTION);
        if (scelta == JOptionPane.YES_OPTION) {
            dispose();
            onCambiaUtente.run();
        }
    }

    private void aggiornaTitolo() {
        setTitle(utente == null ? "Rubrica" : "Rubrica - " + utente.getUsername());
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
