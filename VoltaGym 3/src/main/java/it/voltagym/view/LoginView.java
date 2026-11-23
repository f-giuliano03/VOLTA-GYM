package it.voltagym.view;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;
    private final JTextField emailField = new JTextField(24);
    private final JTextField matricolaField = new JTextField(24);
    private final JButton loginButton = new JButton("ACCEDI");
    public LoginView() {
        super("Volta Gym - Accesso");
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(VoltaTheme.BG);
        root.setBorder(new EmptyBorder(25, 30, 20, 30));
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 8));
        header.setOpaque(false);
        JLabel title = label("VOLTA GYM", VoltaTheme.ACCENT, 28, true);
        JLabel subtitle = label("Accesso riservato agli studenti del Collegio", VoltaTheme.MUTED, 13, false);
        header.add(title); header.add(subtitle);
        root.add(header, BorderLayout.NORTH);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(VoltaTheme.CARD);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        panel.add(label("Email istituzionale:", VoltaTheme.LIGHT, 13, false), c);
        c.gridx = 1; panel.add(emailField, c);
        c.gridy = 1; c.gridx = 0;
        panel.add(label("Numero di matricola:", VoltaTheme.LIGHT, 13, false), c);
        c.gridx = 1; panel.add(matricolaField, c);
        c.gridy = 2; c.gridx = 0; c.gridwidth = 2;
        loginButton.setBackground(VoltaTheme.ACCENT);
        loginButton.setForeground(VoltaTheme.BG);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);
        panel.add(loginButton, c);
        root.add(panel, BorderLayout.CENTER);
        root.add(VoltaTheme.credits(), BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(loginButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); pack(); setLocationRelativeTo(null);
    }
    private static JLabel label(String text, Color color, int size, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return label;
    }
    public String getEmail() { return emailField.getText(); }
    public String getMatricola() { return matricolaField.getText(); }
    public void addLoginListener(ActionListener listener) { loginButton.addActionListener(listener); }
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Accesso non riuscito", JOptionPane.ERROR_MESSAGE);
    }
}
