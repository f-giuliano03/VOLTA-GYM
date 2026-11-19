package it.voltagym.model;
import java.time.LocalDate;import java.util.List;
public interface ReservationRepository {
 List<Reservation> findForGymAndDate(Gym gym,LocalDate date);
 List<Reservation> findByStudent(String email);
 Reservation create(Reservation reservation,List<String> participants);
 boolean cancel(long id,String email);
 List<String> allStudentEmails();
}
