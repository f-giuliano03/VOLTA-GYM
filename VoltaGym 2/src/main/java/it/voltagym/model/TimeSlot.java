package it.voltagym.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Rappresenta una fascia oraria selezionabile. */
public record TimeSlot(LocalTime start, LocalTime end) {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String toString() {
        return FORMAT.format(start) + " - " + FORMAT.format(end);
    }
}
