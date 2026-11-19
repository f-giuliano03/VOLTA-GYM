package it.voltagym.controller;
import java.util.Optional;
import it.voltagym.model.GymService;
import it.voltagym.model.Student;
import it.voltagym.view.GymSelectionView;
import it.voltagym.view.LoginView;
public class AppController {
    private final LoginView view;
    private final GymService service;
    public AppController(LoginView view, GymService service) {
        this.view = view; this.service = service;
        view.addLoginListener(event -> login());
    }
    private void login() {
        try {
            Optional<Student> student = service.authenticate(view.getEmail(), view.getMatricola());
            if (student.isPresent()) {
                GymSelectionView selection = new GymSelectionView(student.get().getEmail());
                new GymSelectionController(selection);
                view.dispose();
                selection.setVisible(true);
            } else {
                view.showError("Credenziali non valide.\\nVerifica email istituzionale e matricola.");
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            view.showError("Problema durante il controllo delle credenziali. Riprova.");
        }
    }
}
