package it.voltagym.model;
import java.util.Optional;
public interface StudentRepository {
    Optional<Student> findByEmailAndMatricola(String email, String matricola);
}
