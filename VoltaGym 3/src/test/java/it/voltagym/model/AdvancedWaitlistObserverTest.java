package it.voltagym.model;

import org.junit.jupiter.api.Test;
import java.sql.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;


class AdvancedWaitlistObserverTest {
    private Connection db() throws SQLException {
        Connection c=DriverManager.getConnection("jdbc:sqlite::memory:");
        try(Statement s=c.createStatement()) {
            s.execute("CREATE TABLE waitlist(id INTEGER PRIMARY KEY,student_email TEXT,gym TEXT,booking_date TEXT,start_time TEXT,end_time TEXT,state TEXT)");
            s.execute("CREATE TABLE reservations(id INTEGER PRIMARY KEY,gym TEXT,booking_date TEXT,start_time TEXT,end_time TEXT)");
            s.execute("CREATE TABLE reservation_participants(reservation_id INTEGER,student_email TEXT)");
            s.execute("CREATE TABLE notifications(id INTEGER PRIMARY KEY,student_email TEXT,message TEXT)");
        }
        return c;
    }
    private Reservation freed(LocalDate day) {
        return new Reservation(1,"alice@unipv.it",Gym.SECCHI,day,LocalTime.of(10,0),LocalTime.of(11,0),TrainingMode.INDIVIDUAL);
    }
    private void waiting(Connection c,String email,LocalDate day,String start,String end) throws SQLException {
        try(PreparedStatement p=c.prepareStatement("INSERT INTO waitlist(student_email,gym,booking_date,start_time,end_time,state) VALUES(?,'SECCHI',?,?,?,'WAITING')")) {
            p.setString(1,email);p.setString(2,day.toString());p.setString(3,start);p.setString(4,end);p.executeUpdate();
        }
    }
    private int count(Connection c,String table) throws SQLException {
        try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM "+table)) {rs.next();return rs.getInt(1);}
    }
    @Test void allEligibleWaitingStudentsAreNotified() throws Exception {
        try(Connection c=db()) {
            var day=LocalDate.now().plusDays(1);
            waiting(c,"marco@unipv.it",day,"10:00","11:00");
            waiting(c,"giulia@unipv.it",day,"10:00","11:00");
            new WaitlistService().onSlotFreed(c,freed(day));
            assertEquals(2,count(c,"notifications"));
            assertEquals(0,count(c,"reservations"));
        }
    }
    @Test void notificationIsNotDuplicatedForSameFreedSlot() throws Exception {
        try(Connection c=db()) {
            var day=LocalDate.now().plusDays(1);
            waiting(c,"marco@unipv.it",day,"10:00","11:00");
            var observer=new WaitlistService();
            observer.onSlotFreed(c,freed(day));
            observer.onSlotFreed(c,freed(day));
            assertEquals(1,count(c,"notifications"));
        }
    }
    @Test void largerRequestedIntervalIsNotNotifiedIfOnlyPartIsFreed() throws Exception {
        try(Connection c=db()) {
            var day=LocalDate.now().plusDays(1);
            waiting(c,"marco@unipv.it",day,"09:30","11:30");
            try(PreparedStatement p=c.prepareStatement("INSERT INTO reservations(gym,booking_date,start_time,end_time) VALUES('SECCHI',?,'09:00','10:00')")) {
                p.setString(1,day.toString());p.executeUpdate();
            }
            new WaitlistService().onSlotFreed(c,freed(day));
            assertEquals(0,count(c,"notifications"));
        }
    }
    @Test void cancellationForTodayDoesNotNotifyTomorrowWaitlist() throws Exception {
        try(Connection c=db()) {
            var tomorrow=LocalDate.now().plusDays(1);
            waiting(c,"marco@unipv.it",tomorrow,"10:00","11:00");
            new WaitlistService().onSlotFreed(c,freed(LocalDate.now()));
            assertEquals(0,count(c,"notifications"));
        }
    }
}

//Verifica situazioni più articolate, come l'invio di notifiche
//a più studenti, l'assenza di notifiche duplicate e il caso
//in cui una fascia oraria sia stata liberata soltanto parzialmente
