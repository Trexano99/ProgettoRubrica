# Ricerca: dispositivi controllabili da una nostra app

Prima ricognizione tecnica per la card "Mappare i dispositivi integrabili e le loro API/SDK".
Domanda: esistono dispositivi che possiamo pilotare liberamente da una nostra app mobile, e con
quali funzioni? Possiamo arrivare a venderli con il nostro marchio?

Aggiornata a: ottobre 2026.

## In breve

1. **Tecnicamente è fattibile.** Ci sono almeno due strade solide per pilotare dispositivi da
   un'app mobile nostra: l'**SDK nativo Lovense** (ufficiale, iOS e Android, Bluetooth diretto) e
   la libreria open source **Buttplug.io** (licenza BSD-3, 873 configurazioni di dispositivi di
   oltre 100 marchi).
2. **Lovense è il candidato migliore per partire.** Ha un programma sviluppatori gratuito e
   documentato, le funzioni più ricche (vibrazione, rotazione, spinta, aspirazione, profondità,
   oscillazione) e circa 36 modelli Bluetooth.
3. **Rischio strategico su Lovense.** A novembre 2025 Lovense ha lanciato il proprio AI Companion
   dentro la sua app e a gennaio 2026 una bambola AI. È un concorrente diretto che controlla il
   nostro accesso ai suoi dispositivi: non conviene dipendere solo da lui.
4. **Gli altri grandi marchi non hanno SDK pubblici** (We-Vibe, Satisfyer, Svakom, Lelo, Magic
   Motion). Si possono pilotare solo con i protocolli ricostruiti da Buttplug.io, senza garanzie
   del produttore. Fanno eccezione Kiiroo (API per partner, su richiesta) e The Handy (API
   pubblica, ma per un solo tipo di prodotto).
5. **Per vendere con il nostro marchio la strada realistica è l'OEM/ODM**, cioè produttori (in
   genere cinesi) che fanno dispositivi personalizzati con il nostro marchio, con un protocollo
   Bluetooth che controlliamo noi. Lovense non risulta offrire white label: solo affiliazione.
6. **Vincolo importante per la scelta "app mobile":** App Store e Google Play non accettano app
   con chat AI esplicita. Il controllo dei dispositivi è ammesso, il contenuto sessuale esplicito
   generato dall'AI no. Va deciso prima di fissare lo stack (dettagli più sotto).

## Confronto delle opzioni

| Opzione | Come si accede | Dispositivi | Funzioni | Da una nostra app mobile | Accesso | Rischi principali |
|---|---|---|---|---|---|---|
| **Lovense SDK nativo** | SDK iOS (13+) e Android (5.1+), Bluetooth diretto | ~36 modelli Lovense | Tutte quelle Lovense (vedi sotto) | Sì, senza l'app Lovense | Account sviluppatore gratuito + token | Lovense è anche concorrente; termini commerciali da leggere |
| **Lovense Standard/Socket API** | HTTP/WebSocket verso l'app Lovense Remote dell'utente (rete locale o cloud) | Tutti i Lovense | Come sopra | Solo se l'utente ha anche Lovense Remote | Come sopra | Due app per l'utente, latenza del cloud |
| **Buttplug.io** | Libreria Rust (BSD-3), usabile su iOS e Android | 873 configurazioni, 149 protocolli | Vibrazione, rotazione, oscillazione, posizione, costrizione, batteria | Sì, va integrata (es. Flutter + Rust) | Libero, open source | Protocolli ricostruiti: un aggiornamento firmware può romperli; nessun supporto dei produttori |
| **Kiiroo (FeelApps)** | API REST per partner; il comando passa dall'app FeelConnect | Kiiroo (es. Pearl, Onyx, Keon) | Intensità 0-100 e durata | Solo tramite FeelConnect | Chiave partner rilasciata da Kiiroo | Pensato per cam e mance; serve l'approvazione |
| **The Handy** | API cloud v2/v3 via Wi-Fi (chiave di connessione) + modalità Bluetooth | The Handy, Handy 2 (masturbatori lineari) | Posizione, velocità, script sincronizzati | Sì, via cloud | Documentazione pubblica | Una sola categoria di prodotto; con il firmware 4 alcune app di terzi non funzionano più |
| **OEM/ODM a marchio nostro** | Protocollo Bluetooth fornito dal produttore o definito da noi | Quelli che ordiniamo | Quelle che specifichiamo | Sì, controllo totale | Ordine minimo (es. 300 pezzi) | Costi, certificazioni (CE, RoHS, radio), qualità del firmware, assistenza |
| **Hardware aperto** (OSR2/SR6, OSSM) | Protocollo TCode via seriale | Fai da te | Posizione su più assi | Per prototipi | Libero | Non è un prodotto per il consumatore |

## Lovense nel dettaglio

### Soluzioni per sviluppatori

- **SDK nativi**: iOS 13+, Android 5.1+, Windows. L'app si collega al dispositivo via Bluetooth,
  senza passare dall'app Lovense. È la strada che serve a noi.
- **Standard API**: comandi HTTP all'app Lovense Remote dell'utente, in rete locale (porte 20011
  HTTP e 30011 HTTPS; 34567 per Lovense Connect) o tramite il cloud Lovense. L'abbinamento avviene
  con un codice QR: lo generiamo con `POST https://api.lovense.com/api/lan/getQrCode`, l'utente lo
  scansiona con Lovense Remote e Lovense chiama il nostro callback.
- **Socket API** (WebSocket), **JS SDK** (web) e **Toy Events API** (eventi in tempo reale dal
  dispositivo, solo con Lovense Remote).
- **Accesso**: account sviluppatore gratuito e developer token. I termini d'uso commerciale non
  sono verificati: il portale sviluppatori non era raggiungibile da questo ambiente. Vanno letti
  prima di impegnarsi.

### Comandi

| Comando | Cosa fa |
|---|---|
| `GetToys` | Elenco dei dispositivi collegati, con le funzioni disponibili |
| `Function` | Imposta una o più azioni con un'intensità, per `timeSec` secondi (0 = senza limite) |
| `Pattern` | Sequenza di intensità: intervalli di almeno 100 ms, massimo 50 valori |
| `Preset` | Schemi predefiniti: `pulse`, `wave`, `fireworks`, `earthquake` |
| `PatternV2` / `Position` | Posizione per i dispositivi lineari (non disponibili su tutti i canali) |
| `Stop` | Ferma tutto |

| Azione | Intervallo |
|---|---|
| `Vibrate` (anche `Vibrate1`, `Vibrate2`, `Vibrate3` per i motori separati) | 0-20 |
| `Rotate`, `Thrusting`, `Fingering`, `Suction`, `Oscillate`, `All` | 0-20 |
| `Pump`, `Depth` | 0-3 |
| `Stroke` | 0-100 |

Ogni comando può essere indirizzato a un solo dispositivo (`toy`) o a tutti quelli collegati.

### Funzioni per modello

| Modelli | Funzioni |
|---|---|
| Lush, Hush, Domi, Ferri, Ambi, Ridge, Mission, Hyphy, Tenera, Spinel, Gush | Vibrazione |
| Edge, Diamo, Dolce, Gemini | Due vibrazioni indipendenti |
| Lapis | Vibrazione + terzo canale di vibrazione |
| Nora | Vibrazione + rotazione |
| Max | Vibrazione + pompa (costrizione) |
| Osci, Calor, Flexer | Vibrazione + oscillazione |
| Vulse | Vibrazione + aspirazione |
| Gravity | Spinta + vibrazione |
| Solace, Solace Pro | Vibrazione + spinta + profondità |
| Mini Sex Machine | Spinta |

Elenco completo dei modelli Bluetooth riconosciuti da Buttplug.io: Max, Edge, Nora, Velvo, Ambi,
Lush, Lush Anal, Hush, Synth, Domi, Osci, Osci 3, Mission, Mission 2, Ferri, Diamo, Dolce, Gush,
Gush 2, Hyphy, Calor, Flexer, Gemini, Gravity, Tenera, Ridge, Lapis, Vulse, Solace, Solace Pro,
Spinel, Sex Machine, Mini Sex Machine, più due modelli Loveai (Dolp, Fizz).

### Limiti

- Un dispositivo Bluetooth accetta in genere **una sola app collegata alla volta**: se usa la
  nostra app, l'utente non può usare contemporaneamente Lovense Remote.
- Lovense non pubblica il protocollo Bluetooth come contratto stabile: la via ufficiale è l'SDK.

## Buttplug.io nel dettaglio

- **Cos'è**: libreria open source (Rust, licenza BSD-3) che espone un'unica interfaccia per
  centinaia di dispositivi. Funziona su Windows, macOS, Linux, Android, iOS e nel browser (WASM).
- **App di riferimento**: Intiface Central (Flutter + Rust) è pubblicata su App Store e Google
  Play. Dimostra che la libreria si può integrare in un'app mobile e che uno store accetta un'app
  di solo controllo dei dispositivi. Intiface Central è GPL 3 o commerciale, ma noi useremmo
  direttamente la libreria, che è BSD-3.
- **Copertura** (configurazione dispositivi v5.57): 873 configurazioni, 149 protocolli, 139 dei
  quali via Bluetooth LE.

| Marchio | Configurazioni |
|---|---|
| Joyhub | 160 |
| Galaku | 110 |
| Satisfyer | 84 |
| Lovense | 60 (36 Bluetooth + 24 via Lovense Connect) |
| Svakom | circa 55 |
| Magic Motion | 30 |
| Kiiroo | 33 |
| Foreo | 26 |
| We-Vibe | 23 |
| Hismith, Sexverse, Honeyplaybox | circa 20 ciascuno |
| Lelo | 15 |
| The Handy | 4 |

| Tipo di funzione | Protocolli che la supportano |
|---|---|
| Vibrazione | 129 |
| Oscillazione | 37 |
| Rotazione | 24 |
| Posizione con durata (dispositivi lineari) | 18 |
| Livello batteria | 18 |
| Costrizione / pompa | 16 |
| Temperatura | 3 |

- **Limite**: i protocolli sono ricostruiti dalla community, non concessi dai produttori.
  Funzionano, ma un aggiornamento firmware può romperli (è già successo con The Handy firmware 4)
  e non c'è nessun accordo che ci tuteli.

## Kiiroo e The Handy

- **Kiiroo (FeelApps)**: serve una *Partner Key* rilasciata da Kiiroo. Con quella generiamo un
  token valido 24 ore, poi l'utente scansiona un QR nell'app FeelConnect. Endpoint principali:
  stato utente, autorizzazione e `POST /api/v1/user/<id>/device_speed` con intensità 0-100 e
  durata in secondi. Il comando passa sempre dall'app FeelConnect: non è Bluetooth diretto dalla
  nostra app. Esiste anche un "Kiiroo Control SDK", ma la documentazione non era raggiungibile da
  qui.
- **The Handy** (masturbatore lineare): API REST pubblica v2 e v3, via Wi-Fi e cloud, con una
  chiave di connessione per dispositivo. Ha diverse modalità: HAMP (movimento alternato
  continuo), HSSP (script sincronizzati), HDSP (posizione diretta), HSTP (sincronizzazione del
  tempo). La v3 con il firmware 4 aggiunge notifiche in tempo reale. Handy 2 funziona solo con il
  firmware 4. SDK in JavaScript e Java.

## Vendere con il nostro marchio

| Strada | Cosa significa | Valutazione |
|---|---|---|
| Affiliazione Lovense | Fino al 20% di commissione sulle vendite, cookie di 120 giorni | Facile, ma il prodotto resta Lovense |
| Accordo con un produttore (Lovense, Kiiroo, Svakom) | Co-branding o licenza | Nessuna offerta pubblica: va contattato il produttore. Con Lovense è improbabile, dato che fa già il suo AI Companion |
| OEM/ODM a marchio nostro | Il produttore realizza dispositivi su nostra specifica; in alcuni casi fornisce app o SDK personalizzabili. Esempi trovati: WINYI, Evokomoribi (minimo 300 pezzi, certificazioni CE, RoHS, FDA) | **La strada coerente con l'obiettivo**: protocollo nostro, marchio nostro, margine nostro. Richiede investimento, controllo qualità e certificazioni |

Joyhub e Galaku, i marchi con più modelli in Buttplug.io, sono un buon indizio del mercato OEM:
molti modelli economici che condividono pochi protocolli Bluetooth.

## Vincolo degli store per l'app mobile

- **App Store (regola 1.1.4)**: vieta il materiale "apertamente sessuale o pornografico".
  L'aggiornamento di giugno 2026 chiede di tenere conto delle funzioni AI nella classificazione per
  età. Le app AI che possono generare contenuti pornografici non sono ammesse. Flirt e contenuti
  emotivi sono accettati.
- **Google Play**: non ammette contenuti sessuali né app di AI generativa pensate principalmente
  per la gratificazione sessuale.
- **Le app di solo controllo dei dispositivi esistono su entrambi gli store** (Lovense Remote,
  Intiface Central).
- **Conseguenza per noi**: un'app negli store può pilotare i dispositivi, ma la chat AI esplicita
  quasi certamente non può stare dentro l'app. Le strade sono tre:
  - un compagno non esplicito (romantico ed emotivo) nell'app;
  - un'architettura divisa: app nativa per il dispositivo e chat esplicita sul web;
  - su Android, distribuzione fuori da Google Play.

  È una decisione da prendere con Ramis (quadro legale) e con Riccardo (comportamento dell'AI).

## Raccomandazione

1. **Prototipo con Lovense SDK nativo** e due dispositivi: un vibratore semplice (es. Lush) e uno
   multifunzione (es. Nora o Solace). È la strada ufficiale e con più funzioni.
2. **Fin dal primo giorno, un livello di astrazione nostro** tra la chat AI e il dispositivo:
   comandi come intensità 0-1, ritmo, schema, durata, tradotti poi per ogni produttore. Ci rende
   indipendenti da Lovense e si aggancia al lavoro di Riccardo sul formato dei comandi.
3. **Buttplug.io come secondo adattatore**, per coprire gli altri marchi e verificare che
   l'astrazione regga.
4. **In parallelo, contatti esplorativi con 2-3 produttori OEM** per costi, ordine minimo, SDK e
   certificazioni: è la base per il prodotto con il nostro marchio.
5. **Prima di scegliere lo stack**, chiudere la questione store (punto precedente) e leggere i
   termini del programma sviluppatori Lovense.

## Cose da verificare a mano

Il portale sviluppatori Lovense, quello Kiiroo e il sito Buttplug.io erano bloccati dalla rete
dell'ambiente in cui è stata fatta la ricerca. I dati sopra vengono dai repository ufficiali su
GitHub, dal codice della configurazione dispositivi di Buttplug.io e da librerie di terze parti.
Da controllare sui portali ufficiali:

- termini d'uso commerciale del programma sviluppatori Lovense (costi, limiti, obblighi);
- documentazione completa dell'SDK nativo Lovense per iOS e Android;
- condizioni del programma partner Kiiroo e del Kiiroo Control SDK.

## Fonti

- Lovense: [Standard API](https://developer-api.lovense.com/docs/standard-solutions/standard-api.html),
  [Standard_solutions](https://github.com/lovense/Standard_solutions),
  [iOS SDK](https://github.com/lovense/Lovense-iOS-SDK),
  [Android SDK](https://github.com/lovense/Lovense-Android-SDK),
  [lovensepy](https://github.com/koval01/lovensepy) (intervalli e funzioni per modello)
- Lovense AI Companion: [EAN](https://www.ean-online.com/product-news/lovense-launches-ai-companion-your-perfect-partner-who-truly-understands-you/),
  [Engadget, CES 2026](https://www.engadget.com/lovense-launches-an-ai-companion-doll-at-ces-170000490.html)
- Lovense affiliazione: [Affpaying](https://www.affpaying.com/lovense)
- Buttplug.io: [repository](https://github.com/buttplugio/buttplug) (configurazione dispositivi
  `buttplug-device-config-v5.json`, versione 5.57),
  [Intiface Central](https://github.com/intiface/intiface-central)
- Kiiroo: [FeelApp API](https://github.com/dulta/feelapp-api),
  [FeelTechnology](https://developer.feeltechnology.com/)
- The Handy: [Handy SDK su npm](https://www.npmjs.com/package/@ohdoki/handy-sdk),
  [documentazione sviluppatori](https://intercom.help/ohdoki/en/articles/8260137-developer-documentation),
  [firmware 4](https://intercom.help/ohdoki/en/articles/9457599-what-s-new-in-firmware-4)
- OEM: [WINYI](https://www.szwinyi.com/app-controlled-toy/),
  [Evokomoribi](https://www.evokomoribi.com/products/app-controlled-products)
- Store: [App Review Guidelines](https://developer.apple.com/app-store/review/guidelines/),
  [Google Play, contenuti inappropriati](https://support.google.com/googleplay/android-developer/answer/9878810?hl=en),
  [Indie Hackers sulle app AI companion](https://www.indiehackers.com/post/ai-lover-apps-made-162-8m-in-six-months-the-web-only-ones-arent-even-counted-28fd172f13)
