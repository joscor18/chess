package websocket.messages;

public class ErrorMess extends ServerMessage{
    private final String errorMess;

    public ErrorMess(ServerMessageType type, String errorMess) {
        super(type);
        this.errorMess = errorMess;
    }

    public String getErrorMess(){
        return errorMess;
    }
}
