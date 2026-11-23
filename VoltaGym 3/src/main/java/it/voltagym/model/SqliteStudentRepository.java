package it.voltagym.model;
import java.sql.*;
import java.util.Optional;
public class SqliteStudentRepository implements StudentRepository {
    @Override
    public Optional<Student> findByEmailAndMatricola(String email, String matricola) {
        String sql = "SELECT email, matricola FROM students WHERE email = ? AND matricola = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, matricola);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Student(
                    rs.getString("email"), rs.getString("matricola")));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new RuntimeException("Errore durante la verifica delle credenziali.", ex);
        }
    }
}
