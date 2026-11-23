package it.voltagym;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import it.voltagym.controller.AppController;
import it.voltagym.model.Database;
import it.voltagym.model.GymService;
import it.voltagym.model.SqliteStudentRepository;
import it.voltagym.view.LoginView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //avvia la costruzione dell'interfaccia sul thread dedicato agli eventi di Swing
            try {
                Database.initialize(); //prepara il database
                GymService service = new GymService(new SqliteStudentRepository()); //Viene creato GymService e SqliteStudentRepository
                LoginView view = new LoginView(); //viene creato LoginView
                new AppController(view, service); //collega il pulsante di accesso alla logica di autenticazione
                view.setVisible(true);            //(crea AppController)
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Impossibile avviare Volta Gym.\\n" + ex.getMessage(),
                    "Errore di avvio", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

//Il Main inizializza il database, crea il servizio di autenticazione
// e la schermata di login, quindi collega la View al relativo Controller