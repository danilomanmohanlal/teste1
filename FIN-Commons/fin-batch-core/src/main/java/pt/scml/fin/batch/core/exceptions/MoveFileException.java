package pt.scml.fin.batch.core.exceptions;

public class MoveFileException extends RuntimeException {

    public MoveFileException(String message) {
        super(message);
    }

    public MoveFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
