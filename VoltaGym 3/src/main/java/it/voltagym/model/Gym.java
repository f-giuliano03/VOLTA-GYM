package it.voltagym.model;
/** Palestre del Collegio Volta. */
public enum Gym {
    SECCHI("Palestra Secchi"), SPESSI("Palestra Spessi");
    private final String displayName;
    Gym(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
