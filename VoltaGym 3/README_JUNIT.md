# Test automatici JUnit 5 – VoltaGym

Sono stati aggiunti test in `src/test/java/it/voltagym/model`, senza modificare i sorgenti applicativi.

## Esecuzione in Eclipse
1. Importare come **Existing Maven Projects**.
2. Tasto destro sul progetto → **Maven → Update Project**.
3. Tasto destro su `src/test/java` → **Run As → JUnit Test**.

## Da terminale
`mvn test`

## Copertura
- `BookingStrategyTest`: numero partecipanti individuale e gruppo.
- `GymServiceTest`: credenziali valide e non valide, normalizzazione.
- `GymScheduleServiceTest`: orari, apertura prenotazioni, durate non valide.
- `ReservationServiceTest`: solo domani, limite tra palestre, durata massima.

I test usano repository fittizi in memoria per non alterare il database reale.
**Non sono ancora presenti test di integrazione SQLite** per lista d'attesa, notifiche e prenotazioni di gruppo end-to-end.
L'esecuzione non è stata verificata in questo ambiente: occorre lanciare `mvn test` in Eclipse o su un computer con Maven e accesso alle dipendenze.
