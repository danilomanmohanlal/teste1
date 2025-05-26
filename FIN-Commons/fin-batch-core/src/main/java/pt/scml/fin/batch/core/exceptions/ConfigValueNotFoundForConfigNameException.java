package pt.scml.fin.batch.core.exceptions;

public class ConfigValueNotFoundForConfigNameException extends RuntimeException {

    public ConfigValueNotFoundForConfigNameException(String message) {
        super(message);
    }

    public ConfigValueNotFoundForConfigNameException(String message, Throwable cause) {
        super(message, cause);
    }
}