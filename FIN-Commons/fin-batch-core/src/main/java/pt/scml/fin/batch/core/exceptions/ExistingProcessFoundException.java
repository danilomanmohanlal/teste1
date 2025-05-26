package pt.scml.fin.batch.core.exceptions;

public class ExistingProcessFoundException extends RuntimeException {

    public ExistingProcessFoundException(String message) {
        super(message);
    }

    public ExistingProcessFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
