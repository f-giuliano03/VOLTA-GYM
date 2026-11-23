# VoltaGym — Rapporto sui test JUnit 5

## Scopo
Verificare le regole delle prenotazioni e il comportamento delle notifiche del sistema VoltaGym. I test sono separati dal codice applicativo nella cartella `src/test/java`.

## Ambiente
- Java 17, Maven, JUnit Jupiter 5.11.4, Maven Surefire 3.5.2
- SQLite JDBC 3.46.1.0
- Esecuzione in Eclipse: clic destro su `src/test/java` → **Run As → JUnit Test**.
- Esecuzione con Maven: `mvn test`.

## Casi coperti
| Classe | Metodi | Verifiche |
|---|---:|---|
| BookingStrategyTest | 2 | Numero partecipanti individuale e gruppo |
| GymServiceTest | 2 | Funzionalità del servizio palestre |
| GymScheduleServiceTest | 3 | Vincoli temporali e durate |
| ReservationServiceTest | 3 | Data, limite giornaliero e durata |
| WaitlistObserverTest | 4 | Notifiche, occupazione e palestra |
| AdvancedReservationRulesTest | 4 | Disponibilità e conflitti per utente/palestra |
| AdvancedWaitlistObserverTest | 4 | Più destinatari, duplicati, intervalli e data |
| **Totale** | **22** | |

## Risultati
L'utente ha confermato l'esecuzione senza errori dei 14 test della versione precedente. Gli 8 nuovi test sono **da eseguire e verificare in Eclipse**. Non si dichiara una copertura percentuale senza uno strumento di misurazione.

## Limiti della suite
- I test `AdvancedReservationRulesTest` utilizzano un repository fittizio e verificano il servizio, non la persistenza SQLite reale.
- I test degli observer utilizzano un database SQLite in memoria e verificano `onSlotFreed`, non l'intero percorso UI → repository → database → notifica.
- Non è ancora presente un test end-to-end della prenotazione di gruppo con partecipanti reali e annullamento tramite `SqliteReservationRepository`.
- Il controllo della finestra di prenotazione dipende dall'ora di sistema: alcuni test preesistenti possono essere saltati prima delle 07:00.

## Come documentare l'esecuzione
Dopo `Run As → JUnit Test`, annotare **Runs**, **Errors**, **Failures** e **Skipped** e conservare uno screenshot della vista JUnit per la consegna.
