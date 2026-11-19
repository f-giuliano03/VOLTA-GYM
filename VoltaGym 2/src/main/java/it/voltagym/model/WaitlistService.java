package it.voltagym.model;
import java.sql.*;
import java.time.*;
import java.util.*;

/** Persistent waitlist and notification observer. No automatic reservations. */
public final class WaitlistService implements SlotAvailabilityObserver {
    private final GymScheduleService schedule = new GymScheduleService();
    public void join(String email, Gym gym, LocalDate date, TimeSlot slot) {
        if (!date.equals(LocalDate.now().plusDays(1)) || !schedule.isBookingWindowOpen(date, LocalDateTime.now()))
            throw new IllegalStateException("Puoi iscriverti alla lista d'attesa soltanto per domani, dopo le 07:00.");
        if(slot==null || slot.start().isBefore(schedule.getOpeningTime()) || slot.end().isAfter(schedule.getClosingTime())
            || Duration.between(slot.start(),slot.end()).toMinutes()<30 || Duration.between(slot.start(),slot.end()).toMinutes()>120)
            throw new IllegalArgumentException("Fascia oraria non valida.");
        try(Connection c=Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement p=c.prepareStatement("SELECT 1 FROM reservation_participants rp JOIN reservations r ON r.id=rp.reservation_id WHERE rp.student_email=? COLLATE NOCASE AND r.booking_date=?")) {
                    p.setString(1,email);p.setString(2,date.toString());try(ResultSet rs=p.executeQuery()){if(rs.next())throw new IllegalStateException("Hai già una prenotazione per questa giornata.");}
                }
                try(PreparedStatement p=c.prepareStatement("SELECT 1 FROM reservations WHERE gym=? AND booking_date=? AND start_time<? AND end_time>?")) {
                    p.setString(1,gym.name());p.setString(2,date.toString());p.setString(3,slot.end().toString());p.setString(4,slot.start().toString());
                    try(ResultSet rs=p.executeQuery()){if(!rs.next())throw new IllegalStateException("Lo slot è libero: puoi prenotarlo direttamente.");}
                }
                try(PreparedStatement p=c.prepareStatement("INSERT INTO waitlist(student_email,gym,booking_date,start_time,end_time) VALUES(?,?,?,?,?) ON CONFLICT(student_email,gym,booking_date,start_time,end_time) DO UPDATE SET state='WAITING'")) {
                    p.setString(1,email);p.setString(2,gym.name());p.setString(3,date.toString());p.setString(4,slot.start().toString());p.setString(5,slot.end().toString());p.executeUpdate();
                }
                c.commit();
            }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        }catch(SQLException e){throw new RuntimeException("Errore lista d'attesa",e);}
    }
    public List<String> unread(String email) {
        List<String> messages=new ArrayList<>();
        try(Connection c=Database.getConnection()) {
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("SELECT id,message FROM notifications WHERE student_email=? COLLATE NOCASE AND read_at IS NULL ORDER BY id")) {
                p.setString(1,email);
                try(ResultSet rs=p.executeQuery()) {
                    List<Long> ids=new ArrayList<>();
                    while(rs.next()){ids.add(rs.getLong(1));messages.add(rs.getString(2));}
                    try(PreparedStatement update=c.prepareStatement("UPDATE notifications SET read_at=CURRENT_TIMESTAMP WHERE id=?")){
                        for(Long id:ids){update.setLong(1,id);update.executeUpdate();}
                    }
                }
            }
            c.commit();return messages;
        }catch(SQLException e){throw new RuntimeException("Errore lettura notifiche",e);}
    }
    @Override public void onSlotFreed(Connection c,Reservation cancelled)throws SQLException {
        // Notify only when the entire requested interval is now free; exclude users already booked elsewhere.
        String sql="SELECT w.id,w.student_email,w.start_time,w.end_time FROM waitlist w WHERE w.state='WAITING' AND w.gym=? AND w.booking_date=? AND w.start_time<? AND w.end_time>? ORDER BY w.id";
        try(PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,cancelled.gym().name());p.setString(2,cancelled.date().toString());p.setString(3,cancelled.end().toString());p.setString(4,cancelled.start().toString());
            try(ResultSet rs=p.executeQuery()){
                while(rs.next()){
                    long id=rs.getLong(1);String email=rs.getString(2),start=rs.getString(3),end=rs.getString(4);
                    if(!cancelled.date().equals(LocalDate.now().plusDays(1)))continue;
                    try(PreparedStatement busy=c.prepareStatement("SELECT 1 FROM reservations WHERE gym=? AND booking_date=? AND start_time<? AND end_time>? LIMIT 1")){
                        busy.setString(1,cancelled.gym().name());busy.setString(2,cancelled.date().toString());busy.setString(3,end);busy.setString(4,start);
                        try(ResultSet found=busy.executeQuery()){if(found.next())continue;}
                    }
                    try(PreparedStatement own=c.prepareStatement("SELECT 1 FROM reservation_participants rp JOIN reservations r ON r.id=rp.reservation_id WHERE rp.student_email=? COLLATE NOCASE AND r.booking_date=? LIMIT 1")){
                        own.setString(1,email);own.setString(2,cancelled.date().toString());
                        try(ResultSet found=own.executeQuery()){if(found.next())continue;}
                    }
                    String message="Si è liberato uno slot nella palestra "+cancelled.gym().getDisplayName()+" per il "+cancelled.date()+" dalle "+start+" alle "+end+". Prenotalo subito: il posto non è riservato automaticamente.";
                    try(PreparedStatement notify=c.prepareStatement("INSERT INTO notifications(student_email,message) VALUES(?,?)")){
                        notify.setString(1,email);notify.setString(2,message);notify.executeUpdate();
                    }
                    try(PreparedStatement update=c.prepareStatement("UPDATE waitlist SET state='NOTIFIED' WHERE id=?")){
                        update.setLong(1,id);update.executeUpdate();
                    }
                }
            }
        }
    }
}
