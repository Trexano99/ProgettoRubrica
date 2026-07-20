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
persistence.type=file        # oppure mysql (previsto, non ancora implementato)
persistence.file.path=informazioni.txt
```

Senza il file valgono i default. Vedi `rubrica.properties.example`.

## Note

- Il telefono e' obbligatorio: non si puo' salvare un contatto senza numero.
- L'eta deve essere un numero intero.
