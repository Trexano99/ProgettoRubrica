# Board Miro: struttura

Specifica della board Miro di Cumme. Serve sia come riferimento per il team sia come piano per
costruire la board: quando la board cambia, aggiorniamo anche questo file.

- **Nome board**: `Cumme · Project Hub`
- **Link**: _da aggiungere quando la board è creata_

## Layout generale

```
┌──────────────────────────────────────────────────────────────────────────┐
│ 1. INTESTAZIONE  Cumme · missione in una riga · team · link utili        │
├────────────────────────┬────────────────────────┬────────────────────────┤
│ 2a. RICCARDO FIDANZA   │ 2b. MASSIMILIANO       │ 2c. RAMIS              │
│     AI                 │     VISCONTI · Sviluppo│     Business e mktg    │
│     (viola)            │     (blu)              │     (verde)            │
├────────────────────────┴────────────────────────┴────────────────────────┤
│ 3. TASK BOARD                                                            │
│  ┌───────────┐  ┌───────────┐  ┌───────────────┐  ┌───────────┐          │
│  │ Da fare   │→ │ In corso  │→ │ Da verificare │→ │Completato │          │
│  └───────────┘  └───────────┘  └───────────────┘  └───────────┘          │
├──────────────────────────────────────────────────────────────────────────┤
│ 4. ROADMAP  Fase 0 → Fase 1 → Fase 2 → Fase 3 → Fase 4 → Fase 5          │
└──────────────────────────────────────────────────────────────────────────┘
```

## 1. Intestazione

Riquadro in alto con:

- nome del progetto e missione in una riga;
- i tre membri del team, ognuno con il proprio colore;
- link utili (repository GitHub, cartelle condivise, ecc.).

## 2. Spazi personali

Tre frame affiancati, uno per persona, tutti con la stessa struttura. Ognuno gestisce il proprio
spazio liberamente.

| Frame | Persona | Area | Colore |
|---|---|---|---|
| 2a | Riccardo Fidanza | Intelligenza artificiale | Viola |
| 2b | Massimiliano Visconti | Sviluppo | Blu |
| 2c | Ramis | Business e marketing | Verde |

Sezioni di ogni frame:

1. **Chi sono e focus**: ruolo e obiettivi del periodo.
2. **Su cosa sto lavorando**: lavoro in corso, aggiornato da chi lo fa.
3. **Ricerche e fonti**: link, documenti, screenshot, appunti di ricerca.
4. **Idee e note**: spunti ancora da valutare.
5. **Domande aperte e blocchi**: cosa serve dagli altri per andare avanti.

## 3. Task board (Kanban)

Quattro colonne, da sinistra a destra:

| Colonna | Significato |
|---|---|
| **Da fare** | Task definito e assegnato, non ancora iniziato |
| **In corso** | Qualcuno ci sta lavorando adesso |
| **Da verificare** | Lavoro finito da chi lo ha in carico, in attesa di revisione da un altro membro |
| **Completato** | Verificato e chiuso |

### Card

Ogni task è una Miro Card con:

- **titolo**: verbo + oggetto (es. "Mappare le API dei dispositivi");
- **assegnatario**: una sola persona;
- **tag area**: `AI`, `Dev`, `Business` (stesso colore dello spazio personale);
- **scadenza**, se esiste;
- **descrizione**: cosa significa "finito" e i link utili.

### Regole

- Una card ha un solo responsabile. Se un task coinvolge più persone, va diviso in più card.
- Chi lavora su un task sposta la card quando cambia lo stato.
- Da "Da verificare" a "Completato" la sposta **chi verifica**, non chi ha fatto il lavoro.
- Al massimo 2 card "In corso" per persona, per chiudere le cose prima di aprirne di nuove.

### Task iniziali (colonna "Da fare")

| Task | Assegnatario | Tag |
|---|---|---|
| Definire il perimetro dell'MVP (cosa entra nella prima versione e cosa no) | Tutti, una card per persona | AI / Dev / Business |
| Mappare i dispositivi esistenti integrabili e le loro API/SDK (es. Lovense, Buttplug.io/Intiface) | Massimiliano | Dev |
| Proof of concept: pilotare un dispositivo da codice | Massimiliano | Dev |
| Proposta di architettura (app, backend, collegamento chat ↔ dispositivo) | Massimiliano | Dev |
| Valutare provider e modelli LLM compatibili con contenuti per adulti (policy d'uso, costi, latenza, self-hosting) | Riccardo | AI |
| Definire come il dialogo si traduce in comandi per il dispositivo (eventi, intensità, ritmo) | Riccardo | AI |
| Prototipo del primo personaggio della chat | Riccardo | AI |
| Analisi di mercato e dei concorrenti | Ramis | Business |
| Quadro legale: GDPR (i dati sulla vita sessuale sono una categoria particolare, art. 9), verifica dell'età, termini d'uso | Ramis | Business |
| Prime ipotesi di modello di business e prezzi | Ramis | Business |

## 4. Roadmap

Striscia orizzontale con le fasi del progetto. Le date si aggiungono quando il team le concorda.

| Fase | Obiettivo |
|---|---|
| 0. Impostazione | Board, repository, ruoli |
| 1. Ricerca e validazione | Mercato, dispositivi, modelli AI, vincoli legali |
| 2. Prototipo | Chat AI che pilota un dispositivo reale |
| 3. MVP | Prima versione usabile da utenti esterni |
| 4. Test con utenti | Feedback e iterazioni |
| 5. Lancio | Go-to-market |
