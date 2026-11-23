package it.voltagym.controller;
import it.voltagym.model.*;
import it.voltagym.view.CalendarView;
import it.voltagym.view.GymSelectionView;
public class GymSelectionController {  //se il login va a buon fine, interviene questo Controller 
										//che serve per la scelta della palestra
    private final GymSelectionView view;
    public GymSelectionController(GymSelectionView view) {
        this.view=view; view.addSecchiListener(e->select(Gym.SECCHI)); view.addSpessiListener(e->select(Gym.SPESSI));
    } //al suo costruttore vengono collegati i due pulsanti Secchi e Spessi
      //il metodo Select crea il calendario e i servizi necessari per gestire gli orari
      //e le prenotazioni
    private void select(Gym gym) {  //Questo metodo crea il calendario e i servizi necessari
    								//Per gestire gli orari e le prenotazioni.
        CalendarView calendar=new CalendarView(view.getEmail(),gym); //mostra il calendario della palestra selezionata
        GymScheduleService schedule=new GymScheduleService(); //serve per gestire gli orari e le durate
        ReservationService reservations=new ReservationService(new SqliteReservationRepository(),schedule); //serve per gestire la logica delle prenotazioni
        new CalendarController(calendar,schedule,reservations); //serve per coordinare le operazioni dell'utente
        view.dispose(); calendar.setVisible(true);
    }
}

//Questo controller gestisce la scelta tra Secchi e Spessi
//Dopo la selezione, prepara la schermata del calendario e i
//servizi necessari alle prenotazione
//la palestra selezionata viene trasmessa a CalendarView così il sistema
//sa su quale palestra operare