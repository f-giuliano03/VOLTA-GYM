package it.voltagym.model;

import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


class AdvancedReservationRulesTest {
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static Reservation reservation(long id, String email, Gym gym, int start, int end) {
        return new Reservation(id,email,gym,TOMORROW,LocalTime.of(start,0),LocalTime.of(end,0),TrainingMode.INDIVIDUAL);
    }
    private static ReservationService service(Reservation... reservations) {
        var repo = new ReservationServiceTest.FakeRepository();
        repo.reservations.addAll(Arrays.asList(reservations));
        return new ReservationService(repo,new GymScheduleService());
    }
    @Test void occupiedSlotIsNotOfferedToAnotherStudent() { //VERIFICA CHE UNA FASCIA OCCUPATA NON DEVE ESSERE MOSTRATA AD UN ALTRO STUDENTE
        var slots=service(reservation(1,"alice@unipv.it",Gym.SECCHI,10,11))
            .getAvailableSlots("marco@unipv.it",Gym.SECCHI,TOMORROW,60);
        assertFalse(slots.stream().anyMatch(s->s.start().equals(LocalTime.of(10,0))));
    }
    @Test void bookingInSecchiDoesNotOccupySpessiForAnotherStudent() { //UNA PRENOTAZIONE A SECCHI NON OCCUPA SPESSI PER GLI ALTRI COLLEGIALI
        var slots=service(reservation(1,"alice@unipv.it",Gym.SECCHI,10,11))
            .getAvailableSlots("marco@unipv.it",Gym.SPESSI,TOMORROW,60);
        if (LocalTime.now().isBefore(LocalTime.of(7,0))) {
            assertTrue(slots.isEmpty()); // Booking window is still closed
        } else {
            assertTrue(slots.stream().anyMatch(s->s.start().equals(LocalTime.of(10,0))));
        }
    }
    @Test void alreadyBookedStudentHasNoAvailabilityEvenAtDifferentHours() { //CHI HA GIA' PRENOTATO NON PUO' SCEGLIERE UN ALTRO ORARIO
        assertTrue(service(reservation(1,"alice@unipv.it",Gym.SECCHI,10,11))
            .getAvailableSlots("alice@unipv.it",Gym.SECCHI,TOMORROW,60).isEmpty());
    }
    @Test void pastDayIsNeverOffered() { //VERIFICA CHE NON VENGANO OFFERTI SLOT PER LA DATA CORRENTE
        assertTrue(service().getAvailableSlots("alice@unipv.it",Gym.SECCHI,LocalDate.now(),60).isEmpty());
    }
}

//approfondisce i controlli sulla disponibilità delle palestre.
//Per esempio, verifica che una prenotazione a Secchi non occupi automaticamente
//la stessa fascia oraria a Spessi per un altro collegiale