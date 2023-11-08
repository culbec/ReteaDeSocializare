package exception;

public class ValidatorException extends RuntimeException {
    public ValidatorException(String errorMessage) {
        super(errorMessage);
    }
}
