package websocket.messages;

public class NotifMess extends ServerMessage{
    private final String notifMess;


    public NotifMess(ServerMessageType type, String notifMess) {
        super(type);
        this.notifMess = notifMess;
    }

    public String getNotifMess(){
        return notifMess;
    }
}
