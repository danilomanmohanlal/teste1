package pt.scml.fin.batch.core.exceptions;

public class CustomDatabaseQueryException extends RuntimeException {

    public CustomDatabaseQueryException(String message) {
        super(message);
    }

    public CustomDatabaseQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
