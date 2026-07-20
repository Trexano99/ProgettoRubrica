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
| `utenze` | classe `Utente`, finestra di login, registrazione di un nuovo utente | previsto |
| `toolbarAdd` | `JToolBar` con icone, cambio utente e modifica dell'utenza corrente | previsto |
| `databaseIntegration` | persistenza su database MySQL via JDBC | previsto |

### `salvataggioSingoliFile`

Nuovo backend `DirectoryRubricaRepository`, selezionabile con
`persistence.type=directory`: vedi [Backend `directory`](#backend-directory-un-file-per-contatto).
Il backend su file singolo resta disponibile e resta il default, quindi il
comportamento richiesto dai requisiti obbligatori e' invariato.
