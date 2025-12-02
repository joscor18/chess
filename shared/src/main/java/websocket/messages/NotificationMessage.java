package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private final String notifMess;


    public NotificationMessage(String notifMess) {
        super(ServerMessageType.NOTIFICATION);
        this.notifMess = notifMess;
    }

    public String getNotifMess(){
        return notifMess;
    }
}
