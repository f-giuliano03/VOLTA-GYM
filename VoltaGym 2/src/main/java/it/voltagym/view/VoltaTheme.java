package it.voltagym.view;
import java.awt.*;
import javax.swing.*;
/** Palette blu e grigio e firma condivisa tra le schermate. */
public final class VoltaTheme {
    public static final Color BG = new Color(27, 43, 68);
    public static final Color CARD = new Color(52, 67, 87);
    public static final Color ACCENT = new Color(73, 150, 223);
    public static final Color LIGHT = new Color(245, 247, 250);
    public static final Color MUTED = new Color(193, 202, 214);
    public static final Color DISABLED = new Color(130, 144, 161);
    public static final String CREDITS = "Projected by Edoardo Buono e Francesco Giuliano";
    private VoltaTheme() { }
    public static JLabel credits() {
        JLabel label = new JLabel(CREDITS, SwingConstants.CENTER);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return label;
    }
}
