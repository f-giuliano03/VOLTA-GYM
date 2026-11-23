package it.voltagym.model;
public final class IndividualBookingStrategy implements BookingStrategy { public void validate(int n){if(n!=1)throw new IllegalArgumentException("Allenamento individuale: un solo partecipante.");} }
