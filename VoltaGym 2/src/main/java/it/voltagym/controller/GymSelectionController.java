package it.voltagym.controller;
import it.voltagym.model.*;
import it.voltagym.view.CalendarView;
import it.voltagym.view.GymSelectionView;
public class GymSelectionController {
    private final GymSelectionView view;
    public GymSelectionController(GymSelectionView view) {
        this.view=view; view.addSecchiListener(e->select(Gym.SECCHI)); view.addSpessiListener(e->select(Gym.SPESSI));
    }
    private void select(Gym gym) {
        CalendarView calendar=new CalendarView(view.getEmail(),gym);
        GymScheduleService schedule=new GymScheduleService();
        ReservationService reservations=new ReservationService(new SqliteReservationRepository(),schedule);
        new CalendarController(calendar,schedule,reservations);
        view.dispose(); calendar.setVisible(true);
    }
}
