# Test aggiuntivi – VoltaGym

Questa versione conserva il codice applicativo e i test JUnit precedenti. Aggiunge `WaitlistObserverTest` (4 test) che usa esclusivamente un database SQLite **in memoria**, senza modificare `voltagym.db`.

## Casi coperti
- La cancellazione di uno slot notifica chi è in attesa e **non** effettua una prenotazione automatica.
- Uno slot ancora occupato non genera notifiche.
- Un collegiale già prenotato in un'altra palestra non riceve l'avviso.
- La lista d'attesa di un'altra palestra non riceve avvisi non pertinenti.

## Esecuzione
In Eclipse: clic destro su `src/test/java` > **Run As > JUnit Test**. In alternativa: `mvn test` dalla cartella che contiene `pom.xml`.

## Limiti
Questi sono test di integrazione mirati all'Observer con SQLite in memoria, non test end-to-end della GUI né dell'intero ciclo `join`/`cancel` su database persistente. I test preesistenti includono un test condizionale che può essere saltato prima delle 07:00. Il risultato complessivo va verificato in Eclipse: non è stato eseguito qui.
