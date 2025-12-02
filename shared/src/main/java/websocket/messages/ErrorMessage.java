package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;

    public ErrorMessage(String errorMess) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMess;
    }

    public String getErrorMess(){
        return errorMessage;
    }
}
