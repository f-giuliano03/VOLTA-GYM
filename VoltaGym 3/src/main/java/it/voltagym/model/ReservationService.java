package it.voltagym.model;
import java.time.*;
import java.util.List;

public class ReservationService { //E' uno dei servizi centrali del codice
    private final ReservationRepository repository;
    private final GymScheduleService schedule;
    public ReservationService(ReservationRepository repository,GymScheduleService schedule) {
        this.repository=repository; this.schedule=schedule;
    } 																							//viene controllata la disponibilità degli slot
    public List<TimeSlot> getAvailableSlots(String email,Gym gym,LocalDate date,int duration) { //restituisce gli slot prenotabili, escludendo quelli occupati e
    																						    //applicando il limite giornaliero
        List<Reservation> busy=repository.findForGymAndDate(gym,date);
        List<Reservation> own=repository.findByStudent(email); 
        // Ogni collegiale può utilizzare un solo slot al giorno, in una qualunque delle due palestre.
        if (own.stream().anyMatch(r -> r.date().equals(date))) {
            return List.of();
        }
        return schedule.getAvailableSlots(date,duration).stream() 
            .filter(slot -> busy.stream().noneMatch(r -> slot.start().isBefore(r.end()) && slot.end().isAfter(r.start())))
            .toList();
    }
    public List<Reservation> getMyReservations(String email) { //recupera le prenotazioni di un collegiale
        return repository.findByStudent(email);
    }
    public boolean cancel(long id,String email) { //richiede l'annullamento di una prenotazione
        return repository.cancel(id,email);
    }
    public List<String> getOtherStudents(String email){return repository.allStudentEmails().stream().filter(s->!s.equalsIgnoreCase(email)).toList();} //Recupera gli altri collegiali registrati
    public Reservation book(String email,Gym gym,LocalDate date,TimeSlot slot,TrainingMode mode,List<String> participants) { //il metodo riceve esattamente le sei info che gli sono trasm dal Controller
    																														 //Prima di procedere, verifica le regole. Se anche una sola regola è violata
    																														//solleva un'eccezione e interrompe la richiesta
        if(!date.equals(LocalDate.now().plusDays(1)))
            throw new IllegalStateException("Puoi prenotare esclusivamente per il giorno successivo a oggi.");
        if(!schedule.isBookingWindowOpen(date,LocalDateTime.now()))
            throw new IllegalStateException("Le prenotazioni per domani si aprono alle 07:00 di oggi.");
        if(slot==null) throw new IllegalArgumentException("Seleziona una fascia oraria.");
        // Controllo applicativo: vale per entrambe le palestre e per qualsiasi orario della giornata.
        if (repository.findByStudent(email).stream().anyMatch(r -> r.date().equals(date))) { //primo punto in cui viene controllato il limite di prenotazioni (restituisce le prenotaz alle quali lo studente partecipa)
            throw new IllegalStateException("Hai già utilizzato il tuo unico slot giornaliero per questa data. È consentita una sola prenotazione al giorno, della durata massima di due ore, indipendentemente dalla palestra.");
        }
        long minutes=Duration.between(slot.start(),slot.end()).toMinutes();
        if(minutes<30 || minutes>120 || minutes%30!=0 || slot.start().isBefore(schedule.getOpeningTime()) || slot.end().isAfter(schedule.getClosingTime()))
            throw new IllegalArgumentException("La fascia oraria selezionata non è valida.");
        return repository.create(new Reservation(0,email,gym,date,slot.start(),slot.end(),mode),participants); //crea un oggetto Reservation contenente le info della prenotazione
        																									   //delega la registrazione al Repository, fornendo la lista dei partecipanti
    }
}
    
    //se i controlli applicativi sono superati, chiama repository.create

//nel metodo book viene controllato che il collegiale non abbia 
    //già utilizzato il suo slot giornaliero. Questo limite vale 
    //indipendentemente dalla palestra scelta.
    //Questo service controlla anche la data, l'apertura della finestra 
    //di premnotazione, la durata e gli orari.
