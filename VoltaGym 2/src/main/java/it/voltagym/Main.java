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
        SwingUtilities.invokeLater(() -> {
            try {
                Database.initialize();
                GymService service = new GymService(new SqliteStudentRepository());
                LoginView view = new LoginView();
                new AppController(view, service);
                view.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Impossibile avviare Volta Gym.\\n" + ex.getMessage(),
                    "Errore di avvio", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
