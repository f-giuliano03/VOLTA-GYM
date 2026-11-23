package it.voltagym.view;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import it.voltagym.model.Gym;

/** Schermata grafica moderna per la scelta della palestra. */
public class GymSelectionView extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BG = VoltaTheme.BG;
    private static final Color CARD = VoltaTheme.CARD;
    private static final Color ACCENT = VoltaTheme.ACCENT;
    private static final Color LIGHT = VoltaTheme.LIGHT;
    private static final Color MUTED = VoltaTheme.MUTED;
    private final JButton secchiButton = createButton("SCEGLI SECCHI");
    private final JButton spessiButton = createButton("SCEGLI SPESSI");
    private final String email;

    public GymSelectionView(String email) {
        super("Volta Gym - Scelta palestra");
        this.email = email;
        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 34, 30, 34));

        JLabel brand = label("VOLTA GYM", ACCENT, 29, true);
        JLabel heading = label("SCEGLI LA TUA PALESTRA", LIGHT, 20, true);
        JLabel description = label("Seleziona la struttura in cui desideri allenarti", MUTED, 13, false);
        JLabel user = label("Accesso effettuato: " + email, MUTED, 12, false);

        JPanel header = new JPanel(new GridLayout(4, 1, 0, 8));
        header.setOpaque(false);
        header.add(brand); header.add(heading); header.add(description); header.add(user);

        JPanel cards = new JPanel(new GridLayout(1, 2, 20, 0));
        cards.setOpaque(false);
        cards.add(card("01", "PALESTRA SECCHI", secchiButton));
        cards.add(card("02", "PALESTRA SPESSI", spessiButton));

        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 5));
        footer.setOpaque(false);
        footer.add(label("COLLEGIO VOLTA  •  UNIVERSITÀ DI PAVIA", MUTED, 10, false));
        footer.add(VoltaTheme.credits());
        root.add(header, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(690, 390));
        setSize(760, 430);
        setLocationRelativeTo(null);
    }

    private JLabel label(String text, Color color, int size, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return label;
    }

    private JPanel card(String number, String title, JButton button) {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(89, 112, 139)),
            new EmptyBorder(25, 20, 25, 20)));
        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 12));
        texts.setOpaque(false);
        texts.add(label(number, ACCENT, 15, true));
        texts.add(label(title, LIGHT, 17, true));
        panel.add(texts, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        return panel;
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(BG); button.setBackground(ACCENT);
        button.setFocusPainted(false); button.setOpaque(true);
        button.setBorder(new EmptyBorder(13, 12, 13, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public String getEmail() { return email; }

    public void addSecchiListener(ActionListener listener) { secchiButton.addActionListener(listener); }
    public void addSpessiListener(ActionListener listener) { spessiButton.addActionListener(listener); }

    public void showGymSelected(Gym gym) {
        JOptionPane.showMessageDialog(this,
            "Hai selezionato " + gym.getDisplayName() + ".",
            "Palestra selezionata", JOptionPane.INFORMATION_MESSAGE);
    }
}
