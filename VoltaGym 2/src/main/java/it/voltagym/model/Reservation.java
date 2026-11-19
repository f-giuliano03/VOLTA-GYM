package it.voltagym.model;
import java.time.*;
public record Reservation(long id,String studentEmail,Gym gym,LocalDate date,LocalTime start,LocalTime end,TrainingMode mode) {}
