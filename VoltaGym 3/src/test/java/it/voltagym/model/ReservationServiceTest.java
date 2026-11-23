package it.voltagym.model;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ReservationServiceTest {
 static class FakeRepository implements ReservationRepository {
  final List<Reservation> reservations=new ArrayList<>();
  public List<Reservation> findForGymAndDate(Gym gym, LocalDate date) {
   return reservations.stream().filter(r -> r.gym()==gym && r.date().equals(date)).toList();
  }
  public List<Reservation> findByStudent(String email) {
   return reservations.stream().filter(r -> r.studentEmail().equals(email)).toList();
  }
  public Reservation create(Reservation reservation,List<String> participants) {
   reservations.add(reservation);return reservation;
  }
  public boolean cancel(long id,String email){return false;}
  public List<String> allStudentEmails(){return List.of("a@unipv.it","b@unipv.it");}
 }
 private static TimeSlot slot(int startHour,int durationMinutes) {
  LocalTime start=LocalTime.of(startHour,0);
  return new TimeSlot(start,start.plusMinutes(durationMinutes));
 }
 @Test void cannotBookAnyDayOtherThanTomorrow() { //IMPEDISCE DI PRENOTARE UNA DATA DIVERSA DA DOMA I
  var service=new ReservationService(new FakeRepository(),new GymScheduleService());
  assertThrows(IllegalStateException.class, () -> service.book("a@unipv.it",Gym.SECCHI,
   LocalDate.now().plusDays(2),slot(10,60),TrainingMode.INDIVIDUAL,List.of("a@unipv.it")));
 }
 @Test void dailyLimitAppliesAcrossBothGyms() { //VERIFICA IL LIMITE GIORNALIERO TRA LE DUE PALESTRE
  LocalDate tomorrow=LocalDate.now().plusDays(1);
  var repo=new FakeRepository();
  repo.reservations.add(new Reservation(1,"a@unipv.it",Gym.SECCHI,tomorrow,
   LocalTime.of(9,0),LocalTime.of(10,0),TrainingMode.INDIVIDUAL));
  var service=new ReservationService(repo,new GymScheduleService());
  assertTrue(service.getAvailableSlots("a@unipv.it",Gym.SPESSI,tomorrow,60).isEmpty());
 }
 @Test void cannotBookMoreThanTwoHours() { //VERIFICA IL LIMITE MASSIMO DI DURATA
  var service=new ReservationService(new FakeRepository(),new GymScheduleService());
  // The booking window is not open before 07:00; test duration after opening.
  org.junit.jupiter.api.Assumptions.assumeTrue(LocalTime.now().compareTo(LocalTime.of(7,0))>=0);
  assertThrows(IllegalArgumentException.class, () -> service.book("a@unipv.it",Gym.SECCHI,
   LocalDate.now().plusDays(1),slot(10,150),TrainingMode.INDIVIDUAL,List.of("a@unipv.it")));
 }
}

//TEST DELLE REGOLE APPLICATIVE
