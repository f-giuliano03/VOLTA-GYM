package it.voltagym.model;

import org.junit.jupiter.api.Test;
import java.sql.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;


class WaitlistObserverTest {
    private Connection database() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement s = c.createStatement()) {
            s.execute("CREATE TABLE waitlist(id INTEGER PRIMARY KEY,student_email TEXT,gym TEXT,booking_date TEXT,start_time TEXT,end_time TEXT,state TEXT)");
            s.execute("CREATE TABLE reservations(id INTEGER PRIMARY KEY,gym TEXT,booking_date TEXT,start_time TEXT,end_time TEXT)");
            s.execute("CREATE TABLE reservation_participants(reservation_id INTEGER,student_email TEXT)");
            s.execute("CREATE TABLE notifications(id INTEGER PRIMARY KEY,student_email TEXT,message TEXT)");
        }
        return c;
    }
    private Reservation cancelled() {
        return new Reservation(1,"alice@unipv.it",Gym.SECCHI,LocalDate.now().plusDays(1),
            LocalTime.of(10,0),LocalTime.of(11,0),TrainingMode.INDIVIDUAL);
    }
    private void waiting(Connection c, String email, String start, String end) throws SQLException {
        try (PreparedStatement p = c.prepareStatement("INSERT INTO waitlist(student_email,gym,booking_date,start_time,end_time,state) VALUES(?,?,?,?,?,'WAITING')")) {
            p.setString(1,email);p.setString(2,"SECCHI");p.setString(3,LocalDate.now().plusDays(1).toString());
            p.setString(4,start);p.setString(5,end);p.executeUpdate();
        }
    }
    private int count(Connection c, String table) throws SQLException {
        try (Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+table)) {r.next();return r.getInt(1);}
    }
    @Test void cancellationNotifiesWaitingStudentWithoutAutomaticBooking() throws Exception {
        try (Connection c=database()) {
            waiting(c,"marco@unipv.it","10:00","11:00");
            new WaitlistService().onSlotFreed(c,cancelled());
            assertEquals(1,count(c,"notifications"));
            assertEquals(0,count(c,"reservations"),"Notification must not reserve the slot");
            try (Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT state FROM waitlist")) {
                assertTrue(r.next());assertEquals("NOTIFIED",r.getString(1));
            }
        }
    }
    @Test void occupiedIntervalDoesNotTriggerNotification() throws Exception {
        try (Connection c=database()) {
            waiting(c,"marco@unipv.it","10:00","11:00");
            try (PreparedStatement p=c.prepareStatement("INSERT INTO reservations(gym,booking_date,start_time,end_time) VALUES('SECCHI',?,'10:30','11:30')")) {
                p.setString(1,LocalDate.now().plusDays(1).toString());p.executeUpdate();
            }
            new WaitlistService().onSlotFreed(c,cancelled());
            assertEquals(0,count(c,"notifications"));
        }
    }
    @Test void alreadyBookedStudentIsNotNotified() throws Exception {
        try (Connection c=database()) {
            waiting(c,"marco@unipv.it","10:00","11:00");
            try (PreparedStatement p=c.prepareStatement("INSERT INTO reservations(id,gym,booking_date,start_time,end_time) VALUES(2,'SPESSI',?,'12:00','13:00')")) {
                p.setString(1,LocalDate.now().plusDays(1).toString());p.executeUpdate();
            }
            try (Statement s=c.createStatement()) {s.execute("INSERT INTO reservation_participants VALUES(2,'marco@unipv.it')");}
            new WaitlistService().onSlotFreed(c,cancelled());
            assertEquals(0,count(c,"notifications"));
        }
    }
    @Test void unrelatedGymWaitlistIsNotNotified() throws Exception {
        try (Connection c=database()) {
            try (PreparedStatement p=c.prepareStatement("INSERT INTO waitlist(student_email,gym,booking_date,start_time,end_time,state) VALUES('marco@unipv.it','SPESSI',?,'10:00','11:00','WAITING')")) {
                p.setString(1,LocalDate.now().plusDays(1).toString());p.executeUpdate();
            }
            new WaitlistService().onSlotFreed(c,cancelled());
            assertEquals(0,count(c,"notifications"));
        }
    }
}

//Verifica tre regole fondamentali: l'impossibilità di prenotare un giorno
//diverso da domani, il limite giornaliero indipendentemente dalla palestra
//e l'impossibilità di prenotare per più di due ore
