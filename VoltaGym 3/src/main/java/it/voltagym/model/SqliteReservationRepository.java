package it.voltagym.model;
import java.sql.*;import java.time.*;import java.util.*;
public final class SqliteReservationRepository implements ReservationRepository {
 private Reservation map(ResultSet r)throws SQLException {
  return new Reservation(r.getLong("id"),r.getString("student_email"),Gym.valueOf(r.getString("gym")),LocalDate.parse(r.getString("booking_date")),LocalTime.parse(r.getString("start_time")),LocalTime.parse(r.getString("end_time")),TrainingMode.valueOf(r.getString("mode")));
 }
 public List<String> allStudentEmails(){
  List<String> result=new ArrayList<>();
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("SELECT email FROM students ORDER BY email");ResultSet rs=p.executeQuery()){
   while(rs.next())result.add(rs.getString(1));return result;
  }catch(SQLException e){throw new RuntimeException(e);}
 }
 public List<Reservation> findForGymAndDate(Gym gym,LocalDate date){
  List<Reservation> result=new ArrayList<>();
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM reservations WHERE gym=? AND booking_date=? ORDER BY start_time")){
   p.setString(1,gym.name());p.setString(2,date.toString());try(ResultSet rs=p.executeQuery()){while(rs.next())result.add(map(rs));}return result;
  }catch(SQLException e){throw new RuntimeException(e);}
 }
 public List<Reservation> findByStudent(String email){ //questo metodo recupera tute le prenotazioni a cui partecipa un collegiale
  List<Reservation> result=new ArrayList<>();
  String sql="SELECT r.* FROM reservations r JOIN reservation_participants rp ON rp.reservation_id=r.id WHERE rp.student_email=? COLLATE NOCASE ORDER BY r.booking_date,r.start_time";
  try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setString(1,email);try(ResultSet rs=p.executeQuery()){while(rs.next())result.add(map(rs));}return result; //questa query cerca tutte le associazioni relative al collegiale e recupera le prenotazioni
  }catch(SQLException e){throw new RuntimeException(e);}
 }
 public boolean cancel(long id,String email){ //questo metodo controlla che la prenotaz appartenga all'utente che ha richiesto l'annullam
  // Solo l'organizzatore può annullare la prenotazione per l'intero gruppo.
  try(Connection c=Database.getConnection()){
   c.setAutoCommit(false);
   Reservation cancelled=null;
   try(PreparedStatement read=c.prepareStatement("SELECT * FROM reservations WHERE id=? AND student_email=? COLLATE NOCASE")){
    read.setLong(1,id);read.setString(2,email);
    try(ResultSet rs=read.executeQuery()){if(rs.next())cancelled=map(rs);}
   }
   try(PreparedStatement del=c.prepareStatement("DELETE FROM reservations WHERE id=? AND student_email=? COLLATE NOCASE")){
    del.setLong(1,id);del.setString(2,email);
    int count=del.executeUpdate();
    if(count==1){try(PreparedStatement p=c.prepareStatement("DELETE FROM reservation_participants WHERE reservation_id=?")){p.setLong(1,id);p.executeUpdate();}}
    if(count==1 && cancelled!=null)new WaitlistService().onSlotFreed(c,cancelled); //in caso i controlli siano positivi, questo metodo gestisce le 
    																				//notifiche agli studenti in lista d'attesa
    c.commit();return count==1;
   }catch(SQLException e){c.rollback();throw e;}
  }catch(SQLException e){throw new RuntimeException("Errore annullamento",e);}
 }
 public Reservation create(Reservation r,List<String> requested){ //metodo che riceve la richiesta
  List<String> people=requested.stream().map(x->x.trim().toLowerCase(Locale.ROOT)).distinct().toList();
  new java.util.HashSet<>(people); // Lista deduplicata per identificare univocamente i collegiali.
  if(!people.contains(r.studentEmail().toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("L'organizzatore deve partecipare.");
  (r.mode()==TrainingMode.GROUP?new GroupBookingStrategy():new IndividualBookingStrategy()).validate(people.size()); //il sistema controlla la modalità di allenamento scelta
  																													 //dall'utente. Se è GROUP utilizza GroupBookingStrategy
  		//BOOKINGSTRATEGY (STRATEGY PATTERN)																		 //altrimenti utiizza IndividualBookingStrategy
  																												    //Dopo aver selezionato la strategia, richiama validate() passando
  																													//il numero dei partecipanti
  //La strategia non restituisce un valore booleano. Il metodo validate() è di tipo void()
  //se la regola è rispettata, termina normalmente; altrimenti solleva un'eccezione
  try(Connection c=Database.getConnection()){
   try(Statement st=c.createStatement()){st.execute("PRAGMA foreign_keys=ON");st.execute("BEGIN IMMEDIATE");} //questa transazione permette di eseguire 
   																											  //in maniera coordinata le verifiche
   																											  //ed il salvataggio della prenotazione
   try{
    for(String person:people){ //controlliamo ogni singolo componente del gruppo per verificare che sia presente nel db e che non abbia già una prenotaz nella stessa data
     try(PreparedStatement p=c.prepareStatement("SELECT 1 FROM students WHERE email=? COLLATE NOCASE")){p.setString(1,person);try(ResultSet rs=p.executeQuery()){if(!rs.next())throw new IllegalArgumentException("Collegiale non registrato: "+person);}}
     String conflict="SELECT 1 FROM reservation_participants rp JOIN reservations r ON r.id=rp.reservation_id WHERE rp.student_email=? COLLATE NOCASE AND r.booking_date=? LIMIT 1";
     try(PreparedStatement p=c.prepareStatement(conflict)){p.setString(1,person);p.setString(2,r.date().toString());try(ResultSet rs=p.executeQuery()){if(rs.next())throw new IllegalStateException(person+" ha già una prenotazione per questa data, anche nell'altra palestra.");}}
    }
    try(PreparedStatement p=c.prepareStatement("SELECT 1 FROM reservations WHERE gym=? AND booking_date=? AND start_time<? AND end_time>? LIMIT 1")){
     p.setString(1,r.gym().name());p.setString(2,r.date().toString());p.setString(3,r.end().toString());p.setString(4,r.start().toString());
     try(ResultSet rs=p.executeQuery()){if(rs.next())throw new IllegalStateException("Fascia già occupata nella palestra selezionata.");}
    }
    long id;
    try(PreparedStatement p=c.prepareStatement("INSERT INTO reservations(student_email,gym,booking_date,start_time,end_time,mode) VALUES(?,?,?,?,?,?)")){
     p.setString(1,r.studentEmail());p.setString(2,r.gym().name());p.setString(3,r.date().toString());p.setString(4,r.start().toString());p.setString(5,r.end().toString());p.setString(6,r.mode().name());p.executeUpdate();
    }
    try(Statement st=c.createStatement();ResultSet rs=st.executeQuery("SELECT last_insert_rowid()")){rs.next();id=rs.getLong(1);}
    try(PreparedStatement p=c.prepareStatement("INSERT INTO reservation_participants(reservation_id,student_email) VALUES(?,?)")){
     for(String person:people){p.setLong(1,id);p.setString(2,person);p.executeUpdate();}
    }
    try(Statement st=c.createStatement()){st.execute("COMMIT");}
    return new Reservation(id,r.studentEmail(),r.gym(),r.date(),r.start(),r.end(),r.mode());
   }catch(SQLException|RuntimeException e){try(Statement st=c.createStatement()){st.execute("ROLLBACK");}if(e instanceof RuntimeException re)throw re;throw (SQLException)e;}
  }catch(SQLException e){throw new RuntimeException("Errore salvataggio prenotazione",e);}
 }
}


//in questo Repository viene effettuato il secondo controllo
//per verificare il limite di prenotazioni giornaliere grazie alla tabella
//Reservation_partecipants (primo caso d'uso) (per vedere se i partecipanti all'allenamento
//gruppo hanno tutti lo slot di prenotaz disponibile

//P.S. Viene fatto un secondo controllo in questa Repository perchè
//in caso si verifichino due prenotazioni contemporaneamente  il Repository esegue nuovamente
//le verifiche nel momento del salvataggio.
