# Cumme: contesto per Claude

Startup di tre persone che crea esperienze per adulti più immersive grazie all'AI: un compagno
conversazionale che dialoga con l'utente e pilota dispositivi (sex toy) già esistenti.

## Team

- **Riccardo Fidanza**: intelligenza artificiale (chat, modelli, logica dialogo → dispositivo).
- **Massimiliano Visconti**: sviluppo (integrazione dispositivi, app, backend). Lavora in questo repository.
- **Ramis**: business e marketing (mercato, modello di business, legale, go-to-market).

## Convenzioni

- Documentazione e comunicazione in italiano.
- Commit in stile conventional commits, in italiano (es. `feat: ...`, `docs: ...`).
- La struttura della board Miro è in `docs/miro-board.md`: tienila allineata quando la board cambia.

## Vincoli permanenti

- I dati sulla vita sessuale sono una categoria particolare per il GDPR (art. 9): ogni scelta di
  codice, logging, analytics e hosting deve partire da qui.
- Il servizio è solo per adulti: serve una verifica dell'età.

## Decisioni prese

- L'esperienza per l'utente è un'**app mobile** (iOS/Android), senza passare da un prototipo web o desktop.
- Mercato iniziale: **Italia, al massimo Unione Europea**.
- Distribuzione **solo tramite App Store e Google Play**: niente versioni fuori dagli store. Per le
  regole degli store il compagno AI nell'app non può essere esplicito.

## Stato

Fase 0: impostazione. Stack tecnologico non ancora scelto. Prima ricerca sui dispositivi
controllabili in `docs/ricerca-dispositivi.md`.
