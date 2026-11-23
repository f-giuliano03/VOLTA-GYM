package it.voltagym.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BookingStrategyTest {
 @Test void individualRequiresExactlyOne() { //TEST DELL'ALL INDIVIDUALE
  var strategy = new IndividualBookingStrategy();
  assertDoesNotThrow(() -> strategy.validate(1));
  assertThrows(IllegalArgumentException.class, () -> strategy.validate(0));
  assertThrows(IllegalArgumentException.class, () -> strategy.validate(2));
 }
 @Test void groupRequiresTwoToFour() { //TEST DELL'ALLENAM DI GRUPPO
  var strategy = new GroupBookingStrategy();
  for(int n=2;n<=4;n++){ final int participants=n; assertDoesNotThrow(() -> strategy.validate(participants)); }
  assertThrows(IllegalArgumentException.class, () -> strategy.validate(1));
  assertThrows(IllegalArgumentException.class, () -> strategy.validate(5));
 }
}

//questo test verifica che la strategia individuale accetti
//esattamente un partecipante e rifiuti sia zero
//sia due partecipanti
//l'annotazione @Test comunica a JUnit che questo metodo va eseguito
//come Test
//E' UN TEST STRETTAMENTE COLLEGATO ALLO STRATEGYPATTERN