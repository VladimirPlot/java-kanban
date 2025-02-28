package exception;

public class InvalidTaskTimeException extends Exception {
    public InvalidTaskTimeException(String message) {
        super(message);
    }
}