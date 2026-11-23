package it.voltagym.model;
public final class GroupBookingStrategy implements BookingStrategy { public static final int CAPACITY=4; public void validate(int n){if(n<2||n>CAPACITY)throw new IllegalArgumentException("Allenamento di gruppo: da 2 a 4 partecipanti, organizzatore incluso.");} }
