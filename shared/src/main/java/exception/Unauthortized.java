package exception;

public class Unauthortized extends ResponseException {

    public Unauthortized(String message) {
        super(message, 401);
    }
}
