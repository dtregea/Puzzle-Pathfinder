package puzzles.jam.model;

/**
 * JamClientData class
 * Transfer messages across model and view
 */
public class JamClientData {
    private final String message;

    /** Construct a JamClientData
     * @param message Text to be shared across model and view
     */
    public JamClientData(String message) {
        this.message = message;
    }

    /** Print the message of a JamClientData
     * @return the message of a JamClientData
     */
    @Override
    public String toString() {
        return message;
    }
}
