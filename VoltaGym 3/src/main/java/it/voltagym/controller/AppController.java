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
    private void login() { //una volta ricevute le credenziali si avvia questo metodo
        try { 
            Optional<Student> student = service.authenticate(view.getEmail(), view.getMatricola()); //vengono richiamate credenziali dalla view 
            																						//viene restituito un Optimal che può essere pieno 
            																						//o vuoto (autenticazione fallita)
            if (student.isPresent()) { //se l'autenticazione riesce viene creata GymSelectionView 
                GymSelectionView selection = new GymSelectionView(student.get().getEmail());
                new GymSelectionController(selection);  //e si crea anche il suo controller
                view.dispose(); //si chiude la schermata di Login
                selection.setVisible(true);
            } else { //se l'autenticazione non è corretta allora viene mostrato il messaggio di errore
                view.showError("Credenziali non valide.\\nVerifica email istituzionale e matricola.");
            }
        } catch (RuntimeException ex) {  
            ex.printStackTrace();
            view.showError("Problema durante il controllo delle credenziali. Riprova.");
        }
    }
}

//APPController gestisce la fase di autenticazione. Riceve le credenziali dalla schermata di Login,
//richiede la verifica al GymService e, se l'accesso riesce, apre la
//selezione della palestra