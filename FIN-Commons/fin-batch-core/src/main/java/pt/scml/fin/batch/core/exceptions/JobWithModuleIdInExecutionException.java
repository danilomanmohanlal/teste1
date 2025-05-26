package pt.scml.fin.batch.core.exceptions;

public class JobWithModuleIdInExecutionException extends RuntimeException {

    public JobWithModuleIdInExecutionException(String message) {
        super(message);
    }

    public JobWithModuleIdInExecutionException(String message, Throwable cause) {
        super(message, cause);
    }


}
