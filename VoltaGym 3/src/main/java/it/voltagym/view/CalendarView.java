package it.voltagym.view;

import it.voltagym.model.Gym;
import it.voltagym.model.TimeSlot;
import it.voltagym.model.TrainingMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/** Schermata calendario: visualizza giorni e fasce orarie selezionabili. */
public class CalendarView extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BG = VoltaTheme.BG;
    private static final Color CARD = VoltaTheme.CARD;
    private static final Color ACCENT = VoltaTheme.ACCENT;
    private static final Color LIGHT = VoltaTheme.LIGHT;
    private static final Color MUTED = VoltaTheme.MUTED;
    private static final Color DISABLED = VoltaTheme.DISABLED;

    private final String email;
    private final Gym gym;
    private final JLabel monthLabel = label("", LIGHT, 17, true);
    private final JPanel daysPanel = new JPanel(new GridLayout(0, 7, 6, 6));
    private final JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{30, 60, 90, 120});
    private final JComboBox<TrainingMode> modeCombo = new JComboBox<>(TrainingMode.values());
    private final DefaultListModel<TimeSlot> slotModel = new DefaultListModel<>();
    private final JList<TimeSlot> slotsList = new JList<>(slotModel);
    private final JLabel selectedDateLabel = label("", LIGHT, 14, true);
    private final JLabel statusLabel = label("Seleziona una fascia oraria.", MUTED, 12, false);
    private final JButton previousMonthButton = secondaryButton("‹");
    private final JButton nextMonthButton = secondaryButton("›");
    private final JButton summaryButton = primaryButton("CONFERMA PRENOTAZIONE"); //BOTTONE DI CONFERMA
    private final JButton changeGymButton = secondaryButton("CAMBIA PALESTRA");
    private final JButton myBookingsButton = secondaryButton("LE MIE PRENOTAZIONI");
    private final JButton waitlistButton = secondaryButton("LISTA D'ATTESA");
    private final JButton notificationsButton = secondaryButton("AVVISI");

    private LocalDate selectedDate = LocalDate.now().plusDays(1);
    private YearMonth displayedMonth = YearMonth.from(LocalDate.now().plusDays(1));

    public CalendarView(String email, Gym gym) {
        super("Volta Gym - Calendario");
        this.email = email;
        this.gym = gym;
        buildInterface();
        renderCalendar();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 650));
        setSize(1180, 700);
        setLocationRelativeTo(null);
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel header = new JPanel(new GridLayout(3, 1, 0, 5));
        header.setOpaque(false);
        header.add(label("VOLTA GYM", ACCENT, 25, true));
        header.add(label("CALENDARIO • " + gym.getDisplayName().toUpperCase(Locale.ITALIAN), LIGHT, 18, true));
        header.add(label("Utente: " + email, MUTED, 11, false));
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 18, 0));
        content.setOpaque(false);
        content.add(buildCalendarCard());
        content.add(buildSlotsCard());
        root.add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setOpaque(false);
        JLabel opening = label("APERTO TUTTI I GIORNI  •  07:00 – 23:30", MUTED, 11, false);
        footer.add(opening, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(changeGymButton);
        actions.add(myBookingsButton);
        actions.add(waitlistButton);
        actions.add(notificationsButton);
        actions.add(summaryButton);
        footer.add(actions, BorderLayout.EAST);
        JPanel footerArea = new JPanel(new BorderLayout(0, 10));
        footerArea.setOpaque(false);
        footerArea.add(footer, BorderLayout.NORTH);
        footerArea.add(VoltaTheme.credits(), BorderLayout.SOUTH);
        root.add(footerArea, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildCalendarCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(10, 14));
        card.add(label("SCEGLI IL GIORNO", LIGHT, 15, true), BorderLayout.NORTH);

        JPanel monthBar = new JPanel(new BorderLayout(8, 0));
        monthBar.setOpaque(false);
        monthBar.add(previousMonthButton, BorderLayout.WEST);
        monthBar.add(monthLabel, BorderLayout.CENTER);
        monthBar.add(nextMonthButton, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(monthBar, BorderLayout.NORTH);
        daysPanel.setOpaque(false);
        center.add(daysPanel, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        JLabel hint = label("È prenotabile esclusivamente il giorno successivo a oggi.", MUTED, 10, false);
        card.add(hint, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildSlotsCard() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(10, 12));

        JPanel top = new JPanel(new GridLayout(3, 1, 0, 8));
        top.setOpaque(false);
        top.add(label("FASCE ORARIE", LIGHT, 15, true));
        selectedDateLabel.setHorizontalAlignment(SwingConstants.LEFT);
        top.add(selectedDateLabel);

        JPanel durationRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        durationRow.setOpaque(false);
        JLabel durationLabel = label("Durata: ", MUTED, 12, false);
        durationRow.add(durationLabel);
        durationCombo.setSelectedItem(60);
        durationCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        durationRow.add(durationCombo);
        top.add(durationRow);
        card.add(top, BorderLayout.NORTH);

        JPanel trainingRow=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        trainingRow.setOpaque(false);trainingRow.add(label("Allenamento: ",MUTED,12,false));trainingRow.add(modeCombo);top.add(trainingRow);
        slotsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slotsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        slotsList.setFixedCellHeight(31);
        slotsList.setBackground(new Color(238, 244, 248));
        slotsList.setSelectionBackground(ACCENT);
        slotsList.setSelectionForeground(BG);
        card.add(new JScrollPane(slotsList), BorderLayout.CENTER);

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(89, 112, 139)),
                new EmptyBorder(17, 17, 17, 17)));
        return panel;
    }

    private void renderCalendar() {
        monthLabel.setText(displayedMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                + " " + displayedMonth.getYear());
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        daysPanel.removeAll();

        for (DayOfWeek day : DayOfWeek.values()) {
            JLabel dayLabel = label(day.getDisplayName(TextStyle.SHORT, Locale.ITALIAN).toUpperCase(Locale.ITALIAN),
                    MUTED, 10, true);
            daysPanel.add(dayLabel);
        }

        LocalDate first = displayedMonth.atDay(1);
        int offset = first.getDayOfWeek().getValue() - 1;
        int cellCount = 42;
        for (int i = 0; i < cellCount; i++) {
            int dayNumber = i - offset + 1;
            if (dayNumber < 1 || dayNumber > displayedMonth.lengthOfMonth()) {
                JLabel empty = new JLabel("");
                daysPanel.add(empty);
                continue;
            }
            LocalDate date = displayedMonth.atDay(dayNumber);
            JButton dayButton = new JButton(Integer.toString(dayNumber));
            dayButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            dayButton.setFocusPainted(false);
            dayButton.setOpaque(true);
            dayButton.setBorder(BorderFactory.createLineBorder(new Color(89, 112, 139)));
            dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (!date.equals(LocalDate.now().plusDays(1))) {
                dayButton.setEnabled(false);
                dayButton.setBackground(new Color(43, 55, 71));
                dayButton.setForeground(DISABLED);
            } else if (date.equals(selectedDate)) {
                dayButton.setBackground(ACCENT);
                dayButton.setForeground(BG);
            } else {
                dayButton.setBackground(new Color(55, 78, 108));
                dayButton.setForeground(LIGHT);
            }
            dayButton.addActionListener(event -> {
                selectedDate = date;
                renderCalendar();
                fireDateChanged();
            });
            daysPanel.add(dayButton);
        }
        previousMonthButton.setEnabled(false);
        nextMonthButton.setEnabled(false);
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void fireDateChanged() {
        selectedDateLabel.setText(selectedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)));
    }

    public LocalDate getSelectedDate() { return selectedDate; }
    public int getSelectedDurationMinutes() { return (Integer) durationCombo.getSelectedItem(); }
    public TimeSlot getSelectedSlot() { return slotsList.getSelectedValue(); }
    public String getEmail() { return email; }
    public Gym getGym() { return gym; }

    public void showSlots(List<TimeSlot> slots) {
        slotModel.clear();
        for (TimeSlot slot : slots) slotModel.addElement(slot);
        slotsList.clearSelection();
        if (slots.isEmpty()) {
            statusLabel.setText("Nessuna fascia disponibile per questa data e durata.");
        } else {
            statusLabel.setText(slots.size() + " fasce orarie visualizzate.");
        }
        fireDateChanged();
    }

    public void showStatus(String message) { statusLabel.setText(message); }

    public void addPreviousMonthListener(ActionListener listener) { previousMonthButton.addActionListener(listener); }
    public void addNextMonthListener(ActionListener listener) { nextMonthButton.addActionListener(listener); }
    public void addDurationListener(ActionListener listener) { durationCombo.addActionListener(listener);modeCombo.addActionListener(listener); }
    public TrainingMode getSelectedMode(){return (TrainingMode)modeCombo.getSelectedItem();}
    public java.util.List<String> selectGroupMembers(java.util.List<String> students){
        JList<String> list=new JList<>(students.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int result=JOptionPane.showConfirmDialog(this,new Object[]{"Seleziona da 1 a 3 altri collegiali. Tu sei già incluso.",new JScrollPane(list)},"Partecipanti allenamento di gruppo",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
        return result==JOptionPane.OK_OPTION?list.getSelectedValuesList():null;
    }
    public void addSummaryListener(ActionListener listener) { summaryButton.addActionListener(listener); }
    public void addWaitlistListener(ActionListener listener) {waitlistButton.addActionListener(listener);}
    public void addNotificationsListener(ActionListener listener) {notificationsButton.addActionListener(listener);}
    public TimeSlot chooseWaitlistSlot(java.util.List<TimeSlot> slots) {
        Object selected=JOptionPane.showInputDialog(this,"Scegli una fascia occupata (durata selezionata):", "Lista d'attesa",JOptionPane.QUESTION_MESSAGE,null,slots.toArray(),slots.isEmpty()?null:slots.get(0));
        return selected instanceof TimeSlot slot?slot:null;
    }
    public void addMyBookingsListener(ActionListener listener) { myBookingsButton.addActionListener(listener); }
    public void addChangeGymListener(ActionListener listener) { changeGymButton.addActionListener(listener); }

    public void showPreviousMonth() { }

    public void showNextMonth() { }

    private static JLabel label(String text, Color color, int size, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return label;
    }

    private static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(BG);
        button.setBackground(ACCENT);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        return button;
    }

    private static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(LIGHT);
        button.setBackground(new Color(68, 91, 119));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        return button;
    }


    public ReservationChoice chooseReservation(List<it.voltagym.model.Reservation> reservations) {
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non hai ancora prenotazioni.",
                    "Le mie prenotazioni", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        DefaultListModel<it.voltagym.model.Reservation> model = new DefaultListModel<>();
        for (var reservation : reservations) model.addElement(reservation);
        JList<it.voltagym.model.Reservation> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer((component, value, index, selected, focus) -> {
            JLabel item = new JLabel(value.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " • " + value.gym().getDisplayName() + " • "
                    + value.start() + " - " + value.end());
            item.setOpaque(true);
            item.setBorder(new EmptyBorder(7, 8, 7, 8));
            item.setBackground(selected ? ACCENT : Color.WHITE);
            item.setForeground(BG);
            return item;
        });
        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(list),
                "Seleziona la prenotazione da annullare", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && list.getSelectedValue() != null) {
            var selected = list.getSelectedValue();
            return new ReservationChoice(selected.id(), selected.date() + " • "
                    + selected.gym().getDisplayName() + " • " + selected.start() + " - " + selected.end());
        }
        return null;
    }

    public record ReservationChoice(long id, String description) { }

    public void showBookingSuccess(TimeSlot slot) {
        String date = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JOptionPane.showMessageDialog(this,
                "Prenotazione confermata!\\n" + gym.getDisplayName() + "\\nData: " + date
                        + "\\nOrario: " + slot + "\\nDurata: " + getSelectedDurationMinutes() + " minuti",
                "Prenotazione salvata", JOptionPane.INFORMATION_MESSAGE);
    }
}
