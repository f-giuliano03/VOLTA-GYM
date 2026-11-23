package it.voltagym.model;
import java.sql.*;
public final class Database { //DATABASE
    private static final String URL="jdbc:sqlite:voltagym.db";
    private Database(){}
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL); } //APRE UNA CONNESSIONE AL DATABASE
    public static void initialize() { //CREA LE TABELLE NECESSARIE ED ESEGUE LE OP DI INIZIALIZZAZIONE RICHIESTE
        String students="CREATE TABLE IF NOT EXISTS students (email TEXT PRIMARY KEY COLLATE NOCASE, matricola TEXT UNIQUE NOT NULL)";
        String bookings="CREATE TABLE IF NOT EXISTS reservations (id INTEGER PRIMARY KEY AUTOINCREMENT, student_email TEXT NOT NULL COLLATE NOCASE, gym TEXT NOT NULL CHECK(gym IN ('SECCHI','SPESSI')), booking_date TEXT NOT NULL, start_time TEXT NOT NULL, end_time TEXT NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(student_email) REFERENCES students(email))";
        try(Connection c=getConnection(); Statement s=c.createStatement()) {
            s.execute("PRAGMA foreign_keys=ON"); s.execute(students); s.execute(bookings);
            s.execute("CREATE TABLE IF NOT EXISTS reservation_participants (reservation_id INTEGER NOT NULL, student_email TEXT NOT NULL COLLATE NOCASE, PRIMARY KEY(reservation_id,student_email), FOREIGN KEY(reservation_id) REFERENCES reservations(id) ON DELETE CASCADE, FOREIGN KEY(student_email) REFERENCES students(email))");
            try(ResultSet cols=c.getMetaData().getColumns(null,null,"reservations","mode")) {
                if(!cols.next()) s.execute("ALTER TABLE reservations ADD COLUMN mode TEXT NOT NULL DEFAULT 'INDIVIDUAL'");
            }
            // Migrazione: tutte le prenotazioni precedenti appartengono al rispettivo titolare.
            s.execute("INSERT OR IGNORE INTO reservation_participants(reservation_id,student_email) SELECT id,student_email FROM reservations");
            s.execute("CREATE TABLE IF NOT EXISTS waitlist (id INTEGER PRIMARY KEY AUTOINCREMENT, student_email TEXT NOT NULL COLLATE NOCASE, gym TEXT NOT NULL, booking_date TEXT NOT NULL, start_time TEXT NOT NULL, end_time TEXT NOT NULL, state TEXT NOT NULL DEFAULT 'WAITING', created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, UNIQUE(student_email,gym,booking_date,start_time,end_time), FOREIGN KEY(student_email) REFERENCES students(email))");
            s.execute("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, student_email TEXT NOT NULL COLLATE NOCASE, message TEXT NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, read_at TEXT, FOREIGN KEY(student_email) REFERENCES students(email))");
            s.execute("CREATE INDEX IF NOT EXISTS idx_reservations_date_gym ON reservations(booking_date,gym)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_reservations_student ON reservations(student_email)");
            try(PreparedStatement p=c.prepareStatement("INSERT OR IGNORE INTO students(email,matricola) VALUES(?,?)")) {
                String[][] demoStudents = {
                    {"alice.rossi@unipv.it", "700101"},
                    {"marco.bianchi@unipv.it", "700102"},
                    {"giulia.verdi@unipv.it", "700103"},
                    {"luca.ferrari@unipv.it", "700104"},
                    {"sofia.conti@unipv.it", "700105"}
                };
                for (String[] student : demoStudents) {
                    p.setString(1, student[0]);
                    p.setString(2, student[1]);
                    p.executeUpdate();
                }
            }
        } catch(SQLException e) { throw new RuntimeException("Errore durante l'inizializzazione del database.",e); }
    }
}

//la tabella ReservationPartecipants serve per il primo caso d'uso
//in quanto permette di capire al sistemi quali collegiali 
//partecipano ad un allenamento di gruppo
