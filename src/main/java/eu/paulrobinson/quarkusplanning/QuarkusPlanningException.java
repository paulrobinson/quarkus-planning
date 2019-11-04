package eu.paulrobinson.quarkusplanning;

public class QuarkusPlanningException extends Exception {

    public QuarkusPlanningException(String message) {
        super(message);
    }

    public QuarkusPlanningException(String message, Throwable cause) {
        super(message, cause);
    }
}
