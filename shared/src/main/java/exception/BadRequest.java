package exception;

public class BadRequest extends ResponseException{

    public BadRequest(String message) {
        super(message, 400);
    }
}
