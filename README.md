# Rubrica

Applicazione Java Swing per gestire una rubrica di contatti: creazione, modifica
ed eliminazione di persone, con salvataggio persistente tra un avvio e l'altro.
Il progetto nasce come prova per dimostrare la capacita' di realizzare un
semplice programma Java con interfaccia grafica che interagisce sia con file
locali sia con un database. Il lavoro e' diviso in piu' branch: `lavoroBase`
contiene il progetto completo richiesto dalle specifiche, mentre gli altri
branch aggiungono le funzionalita' extra descritte in fondo
([Evoluzioni EXTRA](#evoluzioni-extra)).

Le specifiche complete sono nel documento di progetto:
[docs/Progetto_Rubrica.pdf](docs/Progetto_Rubrica.pdf).

## Requisiti

- JDK 11 o superiore

## Struttura

```
src/rubrica/     codice sorgente (domain, persistence, model, ui, config, app)
test/rubrica/    test JUnit del layer logico
lib/             junit-platform-console-standalone.jar, h2.jar, mysql-connector-j.jar
docs/            documento di design e Javadoc generata
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

Per default i contatti vengono salvati in `informazioni.txt`, nella stessa
cartella del jar. Al primo avvio il file non esiste ancora e la rubrica parte
vuota.

## Configurazione e persistenza

Il modo in cui i dati vengono salvati si sceglie dal file `rubrica.properties`
(opzionale, accanto al jar). Senza il file valgono i default; un modello
commentato e' disponibile in `rubrica.properties.example`.

Sono previsti tre backend di persistenza, selezionati da `persistence.type`:

| `persistence.type` | Dove finiscono i dati | Extra |
|---|---|---|
| `file` (default) | un unico file di testo con tutti i contatti | requisiti base |
| `directory` | una cartella con **un file per contatto** | [Extra 1](#extra-1--salvataggio-su-piu-file-branch-salvataggiosingolifile) |
| `mysql` | tabelle di un database MySQL via JDBC | [Extra 5](#extra-5--persistenza-su-database-branch-databaseintegration) |

```properties
persistence.type=file        # file | directory | mysql   (default: file)
persistence.file.path=informazioni.txt
persistence.directory.path=informazioni
persistence.utenti.path=utenti.txt
```

### Uso con database MySQL — passi per la riproducibilita'

Il pacchetto da inviare per far provare l'applicazione con il backend `mysql`
contiene, come da specifiche:

- `Rubrica.jar`
- `schema_database.sql`
- `credenziali_database.properties`

piu' `rubrica.properties` (con `persistence.type=mysql`) e il driver JDBC
(`lib/mysql-connector-j.jar`). I passi, nell'ordine:

1. **Crea database e tabelle.** Lancia una sola volta lo script fornito sul tuo
   MySQL:

   ```
   mysql -u root -p < schema_database.sql
   ```

   Crea il database `rubrica` con le tabelle `persone` e `utenti`.

2. **Configura le credenziali.** Copia `credenziali_database.properties.example`
   in `credenziali_database.properties` accanto al jar e inserisci i parametri
   del tuo server MySQL:

   ```properties
   db.host=localhost       # ip-server-mysql
   db.port=3306            # porta
   db.database=rubrica
   db.user=root            # username
   db.password=            # password
   ```

   Il backend si sceglie in `rubrica.properties` con `persistence.type=mysql`.
   Il valore di `db.database` deve coincidere con il nome del database creato
   dallo script (`rubrica`).

3. **Verifica il driver.** `lib/mysql-connector-j.jar` deve essere presente: il
   manifest di `Rubrica.jar` lo cita nel `Class-Path`, quindi il backend `mysql`
   funziona anche con il doppio click, purche' il jar del connector resti in
   `lib/` accanto all'applicazione.

4. **Avvia** `Rubrica.jar`. Contatti e utenti vengono ora letti e scritti sul
   database.

## Test

```
./run-tests.sh
```

Richiede `lib/junit-platform-console-standalone.jar` e `lib/h2.jar`. La suite e'
scritta con **JUnit 5 (Jupiter)** e copre l'intero layer logico:

| Area | Classi di test | Tipo |
|---|---|---|
| Dominio | `PersonaTest`, `UtenteTest`, `PasswordHashTest` | unit, oggetti di dominio e hashing password |
| Model | `RubricaTest`, `GestoreUtentiTest` | unit con repository in memoria (fake) |
| Persistenza su file | `FileRubricaRepositoryTest`, `FileUtenteRepositoryTest`, `DirectoryRubricaRepositoryTest`, `PersonaCodecTest`, `UtenteCodecTest`, `RepositoryFactoryTest` | integrazione su file/cartelle temporanee e codec |
| Persistenza MySQL | `MysqlRubricaRepositoryTest`, `MysqlUtenteRepositoryTest` | integrazione JDBC su H2 in modalita' MySQL |
| Config | `AppConfigTest`, `ImpostazioniMysqlTest` | unit, lettura di `rubrica.properties` |
| UI | `IconeTest` | unit, generazione icone Java2D |

I test dei repository JDBC girano su un **H2 in-memory in modalita' MySQL**,
quindi la suite non richiede un vero server MySQL.

## Documentazione

```
./make-javadoc.sh
```

Genera la Javadoc in `build/javadoc/index.html`. Una copia gia' generata e'
inclusa in [docs/javadoc/index.html](docs/javadoc/index.html).

## Note

- Il telefono e' obbligatorio: non si puo' salvare un contatto senza numero.
- L'eta deve essere un numero intero.

## Evoluzioni EXTRA

Oltre ai requisiti obbligatori (branch `lavoroBase`, progetto completo **senza**
aggiunte), il progetto raccoglie alcune delle "Evoluzioni EXTRA" suggerite dal
testo. Ogni extra vive su un proprio branch.

| Branch | Extra | Stato |
|---|---|---|
| `lavoroBase` | progetto base, requisiti obbligatori | completato |
| `salvataggioSingoliFile` | Extra 1 — un file per contatto | completato |
| `utenze` | Extra 2 — classe `Utente` e login | completato |
| `toolbarAdd` | Extra 3 — `JToolBar` con icone | completato |
| `databaseIntegration` | Extra 5 — persistenza su MySQL | completato |

L'Extra 4 (pubblicazione del codice su un sistema di versioning online) e'
soddisfatta dal repository Git stesso.

### Extra 1 — salvataggio su piu' file (branch `salvataggioSingoliFile`)

Nuovo backend `DirectoryRubricaRepository`, selezionabile con
`persistence.type=directory`. I contatti non finiscono piu' in un unico file, ma
in una cartella (`informazioni` per default) con **un file di testo per
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
eliminare un contatto elimina il solo file corrispondente. Il backend su file
singolo resta disponibile e resta il default, quindi il comportamento richiesto
dai requisiti obbligatori e' invariato.

### Extra 2 — utenze e login (branch `utenze`)

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

### Extra 3 — barra degli strumenti (branch `toolbarAdd`)

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

### Extra 5 — persistenza su database (branch `databaseIntegration`)

Backend `mysql`, selezionabile con `persistence.type=mysql`: contatti e utenti
non vanno piu' su file ma su due tabelle di un database MySQL, tramite JDBC.
`MysqlRubricaRepository` e `MysqlUtenteRepository` implementano le stesse
interfacce dei backend su file (`RubricaRepository`, `UtenteRepository`), quindi
model e interfaccia grafica non cambiano di una riga: cambia solo il valore in
`rubrica.properties`. I passi per eseguirlo sono nella sezione
[Uso con database MySQL](#uso-con-database-mysql--passi-per-la-riproducibilita).

A differenza dei backend su file, dove l'`id` dipende dalla posizione di riga,
qui e' la chiave primaria `AUTO_INCREMENT` della tabella, letta dalle chiavi
generate all'insert e stabile fra un avvio e l'altro. Le password restano fuori
dal chiaro: la colonna `password_hash` contiene la stessa forma `salt:digest`
dei backend su file.

Le connessioni passano da `FornitoreConnessioni`, un piccolo strato di
indirezione: in produzione `DriverManagerConnessioni` apre la connessione a
MySQL con i parametri di `credenziali_database.properties`; nei test un H2 in-memory in
modalita' MySQL fa girare la stessa SQL senza un server. Gli errori tecnici
(`SQLException`) vengono avvolti in `PersistenceException`, unchecked, cosi' i
contratti dei repository restano uguali per tutti i backend.

**Connector/J.** JDBC fa parte del JDK, ma il driver MySQL no: va scaricato a
parte (`mysql-connector-j`, un `.jar`) e messo in `lib/mysql-connector-j.jar`.
Il driver serve solo a runtime a chi usa `mysql`: la compilazione e i backend su
file non ne hanno bisogno.

## Licenza

Distribuito con licenza MIT. Vedi il file [LICENSE](LICENSE).
