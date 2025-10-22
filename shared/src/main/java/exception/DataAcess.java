package exception;

public class DataAcess extends ResponseException{
    public DataAcess(String message) {
        super(message, 500);
    }
}
