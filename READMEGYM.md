# VoltaGym

**Sistema di gestione e prenotazione delle palestre del Collegio Volta**

Progetto universitario sviluppato in **Java**, con interfaccia grafica **Swing**, architettura **Model–View–Controller (MVC)** e persistenza dei dati tramite **SQLite**.

## Descrizione

VoltaGym è un'applicazione desktop progettata per consentire ai collegiali autorizzati di consultare e prenotare le fasce orarie delle due palestre del Collegio Volta: **Secchi** e **Spessi**. Il sistema gestisce le regole di prenotazione e le disponibilità degli slot.

## Funzionalità principali

- **Accesso riservato:** autenticazione degli utenti registrati mediante email istituzionale `@unipv.it` e matricola.
- **Selezione palestra:** scelta tra le palestre Secchi e Spessi.
- **Calendario prenotazioni:** visualizzazione e selezione delle fasce orarie disponibili.
- **Regole di prenotazione:** prenotazioni consentite per il giorno successivo; limite individuale di una fascia giornaliera, di durata massima pari a due ore, indipendentemente dalla palestra scelta.
- **Allenamento di gruppo:** indicazione dei partecipanti, affinché la prenotazione venga considerata anche per i collegiali coinvolti, secondo le regole previste dal sistema.
- **Aggiornamento disponibilità:** aggiornamento degli slot a seguito delle modifiche alle prenotazioni e avviso quando una fascia torna disponibile.

## Casi d'uso principali

### UC1 — Prenotazione di una fascia oraria

Il collegiale accede al sistema, seleziona una palestra e una fascia oraria. Il sistema applica le regole previste dalla tipologia di prenotazione, inclusa la gestione dei partecipanti in caso di allenamento di gruppo.

### UC2 — Aggiornamento e notifica della disponibilità

Il sistema aggiorna la disponibilità delle fasce orarie in seguito a modifiche o cancellazioni e gestisce l'avviso relativo a uno slot che torna libero.

## Architettura software

Il progetto segue il pattern **MVC (Model–View–Controller)**:

- **Model:** gestisce dati, regole di dominio, prenotazioni e accesso alla persistenza SQLite.
- **View:** presenta le schermate dell'applicazione attraverso Java Swing e raccoglie le interazioni dell'utente.
- **Controller:** coordina le interazioni tra interfaccia grafica e logica applicativa.

Flusso generale: **Utente → View → Controller → Model → Controller → View**.

## Tecnologie

| Tecnologia | Utilizzo |
| --- | --- |
| Java | Linguaggio di programmazione |
| Java Swing | Interfaccia grafica desktop |
| SQLite | Database locale |
| JDBC | Accesso al database |
| Maven | Gestione del progetto e delle dipendenze |
| Eclipse IDE | Ambiente di sviluppo |
| Git e GitHub | Versionamento e pubblicazione |

## Requisiti

- **JDK** compatibile con la versione specificata nel `pom.xml`.
- **Eclipse IDE** con supporto Maven oppure un altro ambiente Java compatibile.
- Connessione Internet per scaricare le dipendenze Maven al primo avvio, se non sono già presenti nella cache locale.

## Installazione e avvio con Eclipse

1. Clonare il repository o scaricarlo tramite **Code → Download ZIP** su GitHub.
2. Se si è scaricato lo ZIP, estrarlo in una cartella locale.
3. Aprire Eclipse e selezionare **File → Import → Maven → Existing Maven Projects**.
4. Selezionare la cartella del progetto contenente `pom.xml` e completare l'importazione.
5. Attendere il download e la configurazione delle dipendenze Maven.
6. Individuare la classe principale `it.voltagym.Main` e avviarla come **Java Application**.
7. Accedere con un utente di test presente nel database dimostrativo.

> **Nota:** verificare che la classe principale, la versione del JDK e le modalità di inizializzazione del database corrispondano alla versione finale pubblicata.

## Documentazione

La cartella `docs/` contiene la documentazione del progetto, tra cui:

- Documento di visione.
- Specifica dei due casi d'uso.
- Diagramma UML dei casi d'uso.
- Diagramma UML delle classi e relativo sorgente PlantUML (`.puml`).

## Struttura del repository

```text
VoltaGym/
├── src/                 # Codice sorgente e risorse del progetto Maven
├── docs/                # Documentazione e diagrammi UML
├── pom.xml              # Configurazione Maven
├── .gitignore           # Esclusioni per Git
└── README.md            # Presentazione e istruzioni
```

> La struttura effettiva può includere ulteriori file e cartelle. Conservare la struttura Maven originale del progetto.

## Autori

- **Edoardo Buono**
- **Francesco Giuliano**

Progetto universitario relativo alla gestione delle palestre del Collegio Volta.

## Note

Il repository deve contenere soltanto dati dimostrativi e credenziali fittizie. Non pubblicare informazioni personali reali, password o file locali contenenti dati riservati.
