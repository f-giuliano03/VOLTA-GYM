package it.voltagym.model;
import java.time.*;
import java.util.*;
public class GymScheduleService { //questo Service gestisce le regole temporali (7:00-23:30)
    private static final LocalTime OPEN=LocalTime.of(7,0), CLOSE=LocalTime.of(23,30);
    public List<TimeSlot> getAvailableSlots(LocalDate date,int duration) { //questo metodo genera le faasce orarie compatibili con
    																	   // gli orari di apertura e con la durtata richiesta
        if(date==null) throw new IllegalArgumentException("La data è obbligatoria.");
        if(duration<30 || duration>120 || duration%30!=0) throw new IllegalArgumentException("Durata non valida.");
        LocalDateTime now=LocalDateTime.now();
        if(!date.equals(now.toLocalDate().plusDays(1)) || !isBookingWindowOpen(date,now)) return List.of();
        List<TimeSlot> slots=new ArrayList<>();
        for(LocalTime t=OPEN; !t.isAfter(CLOSE.minusMinutes(duration)); t=t.plusMinutes(30))
            slots.add(new TimeSlot(t,t.plusMinutes(duration)));
        return List.copyOf(slots);
    }
    public boolean isBookingWindowOpen(LocalDate date,LocalDateTime now) {
        return date!=null && now!=null && date.equals(now.toLocalDate().plusDays(1))
            && !now.isBefore(date.minusDays(1).atTime(7,0));
    }
    public LocalTime getOpeningTime(){return OPEN;}
    public LocalTime getClosingTime(){return CLOSE;}
}


//P.S. GymScheduleService non controlla da solo se uno slot è
//già occupato. Genera gli slot teoricamente disponibili secondo
//le regole temporali. Sarà ReservationService, interrogando
//il Repository, ad escludere quelli occupati.