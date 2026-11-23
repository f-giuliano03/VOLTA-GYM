package it.voltagym.model;
public class Student {
    private final String email;
    private final String matricola;
    public Student(String email, String matricola) {
        this.email = email; this.matricola = matricola;
    }
    public String getEmail() { return email; }
    public String getMatricola() { return matricola; }
}
