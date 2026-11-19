# VOLTA_GYM — Caso d'uso 2: lista d'attesa e avvisi

Questa versione conserva integralmente il caso d'uso 1 (gruppi con partecipanti identificati) e aggiunge il caso d'uso 2.

## Come provarlo
1. Aprire il progetto Maven in Eclipse ed eseguire `it.voltagym.Main`.
2. Accedere con alice.rossi@unipv.it / 700101 e prenotare per domani uno slot libero (dopo le 07:00).
3. Accedere con marco.bianchi@unipv.it / 700102 e scegliere la stessa palestra e la stessa durata. Premere LISTA D'ATTESA, selezionare lo slot occupato e confermare.
4. Tornare all'utente Alice e annullare la prenotazione da LE MIE PRENOTAZIONI.
5. Accedere nuovamente come Marco: compare l'avviso di disponibilità. Se Marco è già nel calendario, l'app controlla gli avvisi ogni 15 secondi; il pulsante AVVISI permette un controllo immediato.
6. Marco deve ancora prenotare lo slot: non viene assegnato automaticamente.

## Dettagli
- Le nuove tabelle SQLite `waitlist` e `notifications` sono create all'avvio nel database già esistente.
- Il servizio `WaitlistService` implementa `SlotAvailabilityObserver` ed è richiamato durante la cancellazione nella stessa transazione.
- Gli avvisi sono persistenti e vengono contrassegnati come letti quando vengono mostrati.
- Il sistema avvisa gli iscritti con intervallo interamente libero dopo la cancellazione e senza prenotazioni giornaliere già attive.
- Le iscrizioni in lista d'attesa non sono prenotazioni e non bloccano lo slot; più utenti possono ricevere lo stesso avviso.
- La funzionalità di avviso è interna all'applicazione, non invia email o notifiche di sistema.
- Per la prova usare il database condiviso `voltagym.db` nella stessa directory di avvio; due esecuzioni in directory diverse non condividono le informazioni.
