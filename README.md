# Rubrica

Applicazione Java Swing per gestire una rubrica di contatti: creazione, modifica
ed eliminazione di persone, con salvataggio su file tra un avvio e l'altro.

## Requisiti

- JDK 11 o superiore

## Struttura

```
src/rubrica/     codice sorgente (domain, persistence, model, ui, config, app)
test/rubrica/    test JUnit del layer logico
lib/             junit-platform-console-standalone.jar
docs/            documento di design
```

## Build

```
./build.sh        # Linux/macOS o Git Bash
build.bat         # Windows (cmd)
```

Produce `Rubrica.jar` (runnable, Main-Class `rubrica.app.Main`).

## Esecuzione

Doppio click su `Rubrica.jar`, oppure:

```
java -jar Rubrica.jar
```

All'avvio compare la finestra di login: la rubrica si apre solo dopo un accesso
riuscito. Al primo avvio non esiste ancora nessun utente, quindi va creato con
il bottone **Nuovo utente**.

I contatti vengono salvati in `informazioni.txt`, nella stessa cartella del jar.
Al primo avvio il file non esiste ancora e la rubrica parte vuota.

## Test

```
./run-tests.sh
```

Richiede `lib/junit-platform-console-standalone.jar`. Copre il layer logico
(Persona, codec, persistenza su file, configurazione, factory, model).

## Documentazione

```
./make-javadoc.sh
```

Genera la Javadoc in `build/javadoc/index.html`.

## Persistenza

Per default i dati vanno su file di testo (`informazioni.txt`), una riga per
contatto nel formato `nome;cognome;indirizzo;telefono;eta`.

Il backend e' scelto da `rubrica.properties` (opzionale, accanto al jar):

```
persistence.type=file        # file | directory | mysql (previsto, non ancora implementato)
persistence.file.path=informazioni.txt
persistence.directory.path=informazioni
persistence.utenti.path=utenti.txt
```

Senza il file valgono i default. Vedi `rubrica.properties.example`.

### Backend `directory`: un file per contatto

Con `persistence.type=directory` i contatti non finiscono piu' in un unico
file, ma in una cartella (`informazioni` per default) con **un file di testo per
persona**, chiamato `Nome-Cognome.txt` e contenente la stessa riga
`nome;cognome;indirizzo;telefono;eta`.

Persone con nome e cognome uguali non si sovrascrivono: al primo file spetta il
nome senza suffisso, ai successivi un suffisso numerico progressivo.

```
informazioni/
  Mario-Rossi.txt      <- primo Mario Rossi
  Mario-Rossi-2.txt    <- secondo Mario Rossi
  Steve-Jobs.txt
```

I caratteri non ammessi nei nomi di file (spazi, `\ / : * ? " < > |`) vengono
sostituiti con `_`. Modificare nome o cognome di un contatto rinomina il file;
eliminare un contatto elimina il solo file corrispondente.

## Note

- Il telefono e' obbligatorio: non si puo' salvare un contatto senza numero.
- L'eta deve essere un numero intero.

## Evoluzioni extra

Oltre ai requisiti obbligatori, il progetto raccoglie alcune delle "Evoluzioni
EXTRA" suggerite dal testo. Ogni extra vive su un proprio branch, a partire da
`lavoroBase` che contiene il progetto completo **senza** aggiunte.

| Branch | Extra | Stato |
|---|---|---|
| `lavoroBase` | progetto base, requisiti obbligatori | completato |
| `salvataggioSingoliFile` | salvataggio in cartella, un file per contatto, con suffisso numerico per gli omonimi | completato |
| `utenze` | classe `Utente`, finestra di login, registrazione di un nuovo utente | completato |
| `toolbarAdd` | `JToolBar` con icone, cambio utente e modifica dell'utenza corrente | completato |
| `databaseIntegration` | persistenza su database MySQL via JDBC | previsto |

### `salvataggioSingoliFile`

Nuovo backend `DirectoryRubricaRepository`, selezionabile con
`persistence.type=directory`: vedi [Backend `directory`](#backend-directory-un-file-per-contatto).
Il backend su file singolo resta disponibile e resta il default, quindi il
comportamento richiesto dai requisiti obbligatori e' invariato.

### `utenze`

Classe di dominio `Utente` (username + password) e finestra di login come unica
finestra mostrata all'avvio: la finestra principale si apre solo dopo un accesso
riuscito, altrimenti compare il messaggio di login errato. Il titolo della
finestra principale riporta l'utente collegato.

Oltre a quanto chiesto dai requisiti, il login ha un bottone **Nuovo utente**
che apre una finestra di registrazione (username, password, conferma password).
Serve anche al primo avvio, quando nessun utente esiste ancora.

Gli utenti sono persistiti in `utenti.txt`, una riga per utente:

```
username;salt:digest
```

Le password **non** vengono salvate in chiaro. Ogni password ha un salt casuale
di 16 byte e viene trasformata in un digest SHA-256 ripetuto 10.000 volte
(`PasswordHash`); il salt rende diversi i digest di due utenti con la stessa
password, le iterazioni rendono piu' costoso un attacco a forza bruta. La
verifica al login confronta i digest a tempo costante.

### `toolbarAdd`

La finestra principale non tiene piu' i bottoni in basso ma una `JToolBar`
(`BarraStrumenti`) in alto, con icona sopra e testo sotto. Le finestre modali
(login, registrazione, editor persona, profilo utente) restano con i bottoni in
basso, dove sono piu' immediati.

| Finestra | Disposizione | Azioni |
|---|---|---|
| principale | toolbar in alto | Nuovo, Modifica, Elimina, Utenza, Cambia utente |
| editor persona | bottoni in basso | Salva, Annulla |
| login | bottoni in basso | LOGIN, Nuovo utente |
| registrazione | bottoni in basso | Registra, Annulla |
| profilo utente | bottoni in basso | Salva, Annulla |

Le icone della toolbar sono disegnate a runtime con Java2D (`Icone`) invece di
essere caricate da file immagine: il jar resta autosufficiente, senza risorse da
ritrovare a classpath.

Oltre a quanto chiesto dai requisiti, la barra della finestra principale ha due
azioni sull'utenza:

- **Utenza** apre `ProfiloUtenteDialog`, che cambia username e/o password
  dell'utente collegato. Serve la password attuale per confermare, e i campi
  della nuova password possono restare vuoti se si vuole cambiare solo lo
  username. L'utente non viene modificato sul posto: si costruisce un nuovo
  `Utente` con lo stesso id, cosi' un errore di salvataggio non lascia in
  memoria dati gia' cambiati.
- **Cambia utente** chiede conferma, chiude la finestra e riporta al login. La
  rubrica viene ricaricata da capo, quindi la nuova sessione non eredita nulla
  dalla precedente.
