package pt.scml.fin.batch.core.exceptions;

public class DateParseException extends RuntimeException {

    public static final String ERROR_MESSAGE = "It's not possible to parse the date using this formatter ";

    public DateParseException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

}
