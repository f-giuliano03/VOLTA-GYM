package it.voltagym.model;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
class GymServiceTest {
 private final StudentRepository repository = (email, matricola) ->
  email.equals("studente@unipv.it") && matricola.equals("12345")
   ? Optional.of(new Student(email, matricola)) : Optional.empty();
 private final GymService service = new GymService(repository);
 @Test void registeredStudentCanLogIn() {
  assertTrue(service.authenticate(" STUDENTE@UNIPV.IT ", " 12345 ").isPresent());
 }
 @Test void invalidCredentialsAreRejected() {
  assertTrue(service.authenticate("studente@gmail.com", "12345").isEmpty());
  assertTrue(service.authenticate("studente@unipv.it", "wrong").isEmpty());
  assertTrue(service.authenticate("studente@unipv.it", "99999").isEmpty());
  assertTrue(service.authenticate(null, "12345").isEmpty());
 }
}

//Questa classe di test verifica il funzionamento dell'autenticazione.
//Controlla che uno studente registrato possa accedere; che un'email
//istituzionale non venga rifiutata, che una matricola errata non
//permetta l'accesso.
//verifica inoltre che la presenza di spazi superflui o lettere
//maiuscole nell'email non impedisca un login altrimenti valido