# Volta Gym – prenotazioni

Importa il progetto in Eclipse con **File → Import → Maven → Existing Maven Projects** e avvia `Main.java` come Java Application.

Credenziali demo:
- Email: `pincopallo@unipv.it`
- Matricola: `506070`

## Regola della data
È prenotabile esclusivamente il giorno successivo a oggi. Se oggi è il 6, si può prenotare solo il 7, non l'8 o date successive. La finestra si apre alle 07:00 del giorno precedente.

## Funzioni
- Scelta palestra Secchi o Spessi.
- Fasce ogni 30 minuti tra 07:00 e 23:30.
- Durata 30, 60, 90 o 120 minuti.
- Prenotazione salvata nel database SQLite.
- Le fasce già occupate nella palestra selezionata non sono disponibili.
- Uno studente non può creare prenotazioni sovrapposte in due palestre.
- La tabella delle prenotazioni viene creata automaticamente senza eliminare gli studenti già registrati.

La gestione delle prenotazioni è separata in Model, View e Controller.


## Gestione delle prenotazioni personali
Dal calendario, **Le mie prenotazioni** mostra le prenotazioni dello studente autenticato e permette di annullarle. Il database accetta l'annullamento solo se la prenotazione appartiene all'email dell'utente autenticato.

## Regola dello slot giornaliero

Ogni collegiale può effettuare **una sola prenotazione per giorno**, in una delle due palestre (Secchi oppure Spessi), per una durata massima di **120 minuti**. La regola viene verificata sia dal servizio applicativo sia dal repository SQLite. Annullando la prenotazione, lo slot giornaliero torna disponibile, purché la finestra di prenotazione sia ancora aperta.


## Versione finale: colori, crediti e utenti demo

Interfaccia blu e grigio su accesso, scelta palestra e calendario. Ogni schermata mostra: `Projected by Edoardo Buono e Francesco Giuliano`.

Il database SQLite `voltagym.db` viene creato automaticamente al primo avvio nella directory di esecuzione. Sono inseriti (senza duplicati, grazie a `INSERT OR IGNORE`) cinque studenti fittizi:

| Nome fittizio | Email | Matricola |
|---|---|---|
| Alice Rossi | alice.rossi@unipv.it | 700101 |
| Marco Bianchi | marco.bianchi@unipv.it | 700102 |
| Giulia Verdi | giulia.verdi@unipv.it | 700103 |
| Luca Ferrari | luca.ferrari@unipv.it | 700104 |
| Sofia Conti | sofia.conti@unipv.it | 700105 |

I dati esistenti nel database non vengono eliminati. Per testare un database pulito, chiudere l'app e rinominare il file `voltagym.db` esistente prima del riavvio.
