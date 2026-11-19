package it.voltagym.model;
import java.sql.Connection;
import java.sql.SQLException;
/** Observer invoked in the same SQLite transaction as a cancellation. */
public interface SlotAvailabilityObserver {
    void onSlotFreed(Connection connection, Reservation reservation) throws SQLException;
}
