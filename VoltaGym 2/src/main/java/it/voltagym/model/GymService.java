package it.voltagym.model;
import java.util.Locale;
import java.util.Optional;
public class GymService {
    private final StudentRepository repository;
    public GymService(StudentRepository repository) { this.repository = repository; }
    public Optional<Student> authenticate(String email, String matricola) {
        if (email == null || matricola == null) return Optional.empty();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedMatricola = matricola.trim();
        if (!normalizedEmail.matches("^[A-Za-z0-9._%+-]+@unipv\\.it$")) return Optional.empty();
        if (!normalizedMatricola.matches("\\d+")) return Optional.empty();
        return repository.findByEmailAndMatricola(normalizedEmail, normalizedMatricola);
    }
}
