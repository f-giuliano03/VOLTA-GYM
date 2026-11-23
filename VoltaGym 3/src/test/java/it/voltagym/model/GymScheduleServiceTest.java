package it.voltagym.model;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
class GymScheduleServiceTest {
 private final GymScheduleService schedule = new GymScheduleService();
 @Test void openingHoursAreCorrect() {
  assertEquals(LocalTime.of(7,0), schedule.getOpeningTime());
  assertEquals(LocalTime.of(23,30), schedule.getClosingTime());
 }
 @Test void bookingWindowOpensAtSevenForTomorrow() {
  LocalDate today=LocalDate.of(2026,11,19);
  assertFalse(schedule.isBookingWindowOpen(today.plusDays(1), today.atTime(6,59)));
  assertTrue(schedule.isBookingWindowOpen(today.plusDays(1), today.atTime(7,0)));
  assertFalse(schedule.isBookingWindowOpen(today.plusDays(2), today.atTime(8,0)));
 }
 @Test void invalidDurationsAreRejected() {
  LocalDate tomorrow=LocalDate.now().plusDays(1);
  assertThrows(IllegalArgumentException.class, () -> schedule.getAvailableSlots(tomorrow, 20));
  assertThrows(IllegalArgumentException.class, () -> schedule.getAvailableSlots(tomorrow, 150));
  assertThrows(IllegalArgumentException.class, () -> schedule.getAvailableSlots(tomorrow, 45));
 }
}

//verifica le regole temporali del programma