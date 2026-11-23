package it.voltagym.model;
import java.util.Locale;
import java.util.Optional;
public class GymService {  //questa classe si occupa di verificare le credenziali del collegiale
    private final StudentRepository repository;
    public GymService(StudentRepository repository) { this.repository = repository; } //utilizza StudentRepository per verificare che lo studente esista nel database.
    public Optional<Student> authenticate(String email, String matricola) { //metodo principale
        if (email == null || matricola == null) return Optional.empty(); //il risultato è un Optional che contiene lo studente se l'autenticazione riesce
        															     //è vuoto altrimenti
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedMatricola = matricola.trim();
        if (!normalizedEmail.matches("^[A-Za-z0-9._%+-]+@unipv\\.it$")) return Optional.empty();
        if (!normalizedMatricola.matches("\\d+")) return Optional.empty();
        return repository.findByEmailAndMatricola(normalizedEmail, normalizedMatricola);
    }
}
 

//Quando viene chiamato il metodo principale, questo controlla che email e matricola siano presenti;
//Verifica che l'email abbia jl dominio @unipv.it e che la matricola sia composta da numeri
//Usa StudentRepository per verificare che lo studente esista nel database