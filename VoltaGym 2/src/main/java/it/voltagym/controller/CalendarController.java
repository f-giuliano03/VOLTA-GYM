package it.voltagym.controller;
import it.voltagym.model.*;
import it.voltagym.view.CalendarView;
import it.voltagym.view.GymSelectionView;
import javax.swing.*;
import java.util.List;

public class CalendarController {
    private final CalendarView view;
    private final ReservationService reservations;
    private final WaitlistService waitlist=new WaitlistService();
    private final GymScheduleService schedule;
    public CalendarController(CalendarView view,GymScheduleService schedule,ReservationService reservations) {
        this.view=view; this.reservations=reservations;this.schedule=schedule;
        view.addWaitlistListener(e->joinWaitlist());
        view.addNotificationsListener(e->showNotifications());
        new javax.swing.Timer(15000,e->showNotifications()).start();
        view.addPreviousMonthListener(e->view.showPreviousMonth());
        view.addNextMonthListener(e->view.showNextMonth());
        view.addDurationListener(e->refresh());
        view.addSummaryListener(e->book());
        view.addMyBookingsListener(e->manageBookings());
        view.addChangeGymListener(e->changeGym());
        refresh();
        showNotifications();
    }
    private void refresh() {
        List<TimeSlot> slots=reservations.getAvailableSlots(view.getEmail(),view.getGym(),view.getSelectedDate(),view.getSelectedDurationMinutes());
        view.showSlots(slots);
    }
    private void book() {
        TimeSlot slot=view.getSelectedSlot();
        if(slot==null){JOptionPane.showMessageDialog(view,"Seleziona prima una fascia oraria.");return;}
        try {
            java.util.List<String> participants=new java.util.ArrayList<>();
            participants.add(view.getEmail());
            if(view.getSelectedMode()==TrainingMode.GROUP){
                java.util.List<String> chosen=view.selectGroupMembers(reservations.getOtherStudents(view.getEmail()));
                if(chosen==null)return;
                participants.addAll(chosen);
            }
            reservations.book(view.getEmail(),view.getGym(),view.getSelectedDate(),slot,view.getSelectedMode(),participants);
            view.showBookingSuccess(slot); refresh();
        } catch(IllegalArgumentException|IllegalStateException ex) {
            JOptionPane.showMessageDialog(view,ex.getMessage(),"Prenotazione non riuscita",JOptionPane.WARNING_MESSAGE); refresh();
        }
    }
    private void manageBookings() {
        CalendarView.ReservationChoice choice = view.chooseReservation(reservations.getMyReservations(view.getEmail()));
        if (choice == null) return;
        int answer = JOptionPane.showConfirmDialog(view,
                "Vuoi annullare questa prenotazione?\\n" + choice.description(),
                "Conferma annullamento", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            boolean removed = reservations.cancel(choice.id(), view.getEmail());
            JOptionPane.showMessageDialog(view, removed ? "Prenotazione annullata." : "Impossibile annullare la prenotazione.");
            refresh();
        }
    }
    private void joinWaitlist() {
        try {
            List<TimeSlot> all=schedule.getAvailableSlots(view.getSelectedDate(),view.getSelectedDurationMinutes());
            List<TimeSlot> free=reservations.getAvailableSlots(view.getEmail(),view.getGym(),view.getSelectedDate(),view.getSelectedDurationMinutes());
            List<TimeSlot> occupied=all.stream().filter(slot->!free.contains(slot)).toList();
            if(occupied.isEmpty()){JOptionPane.showMessageDialog(view,"Non ci sono slot occupati per questa durata.");return;}
            TimeSlot slot=view.chooseWaitlistSlot(occupied);
            if(slot==null)return;
            waitlist.join(view.getEmail(),view.getGym(),view.getSelectedDate(),slot);
            JOptionPane.showMessageDialog(view,"Iscrizione alla lista d'attesa registrata. Riceverai un avviso nell'app quando lo slot sarà libero.");
        }catch(RuntimeException ex){JOptionPane.showMessageDialog(view,ex.getMessage(),"Lista d'attesa",JOptionPane.WARNING_MESSAGE);}
    }
    private void showNotifications() {
        try {
            List<String> messages=waitlist.unread(view.getEmail());
            if(!messages.isEmpty()) {
                JOptionPane.showMessageDialog(view,String.join("\n\n",messages),"VOLTA GYM - Slot libero!",JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        }catch(RuntimeException ex){view.showStatus("Errore durante la lettura degli avvisi: "+ex.getMessage());}
    }
    private void changeGym() {
        GymSelectionView selection=new GymSelectionView(view.getEmail()); new GymSelectionController(selection);
        view.dispose(); selection.setVisible(true);
    }
}
