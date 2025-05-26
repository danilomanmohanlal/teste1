package pt.scml.fin.batch.core.exceptions;

public class CreateDirectoryException extends RuntimeException {

    public CreateDirectoryException(String message) {
        super(message);
    }

    public CreateDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
