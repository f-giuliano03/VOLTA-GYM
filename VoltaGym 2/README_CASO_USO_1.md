# VOLTA_GYM — Solo caso d'uso 1: allenamento individuale e di gruppo

Questa versione parte dal progetto originale Blu-Grigio, senza lista d'attesa o notifiche.

## Modalità
- Individuale: il collegiale prenota per sé.
- Di gruppo: l'organizzatore seleziona da 1 a 3 altri collegiali dall'elenco degli utenti registrati. Totale 2–4 persone, organizzatore incluso.
- Ogni membro viene salvato nella tabella `reservation_participants` e risulta occupato per l'intera giornata, in entrambe le palestre.
- La prenotazione di gruppo occupa la fascia scelta in esclusiva: non si possono sovrapporre altre prenotazioni nella stessa palestra.
- Solo l'organizzatore può annullare l'intera prenotazione. I partecipanti possono vederla in 'Le mie prenotazioni'.
- Per semplicità di questa versione, non è prevista la conferma individuale degli invitati: l'organizzatore li inserisce direttamente.

## Test suggerito
1. Accedere come alice.rossi@unipv.it (matricola 700101), scegliere un allenamento di gruppo per domani e selezionare marco.bianchi@unipv.it e giulia.verdi@unipv.it.
2. Accedere come Marco (700102) e verificare che la prenotazione compaia tra le proprie prenotazioni e che non possa prenotare un altro slot, nemmeno in un'altra palestra.
3. Accedere come Giulia (700103) e verificare lo stesso.
4. Accedere come Sofia (700105) e verificare che possa prenotare una fascia diversa.
5. Accedere come Alice, annullare la prenotazione di gruppo e verificare che Marco e Giulia possano prenotare nuovamente.
6. Provare un gruppo con 1 solo partecipante o con più di 4 partecipanti: deve essere rifiutato.

Nota: il progetto usa SQLite `voltagym.db` nella directory di esecuzione. Eseguire il progetto in Eclipse come progetto Maven; è richiesta Java 17 o superiore. Il database esistente viene migrato automaticamente all'avvio.
