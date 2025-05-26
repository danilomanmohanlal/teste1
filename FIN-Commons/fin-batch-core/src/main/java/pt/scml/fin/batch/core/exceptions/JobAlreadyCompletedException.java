package pt.scml.fin.batch.core.exceptions;

public class JobAlreadyCompletedException extends RuntimeException {

    public JobAlreadyCompletedException(String message) {
        super(message);
    }
}
