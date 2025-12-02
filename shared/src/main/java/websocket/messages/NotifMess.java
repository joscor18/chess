package websocket.messages;

public class NotifMess extends ServerMessage{
    private final String notifMess;


    public NotifMess(String notifMess) {
        super(ServerMessageType.NOTIFICATION);
        this.notifMess = notifMess;
    }

    public String getNotifMess(){
        return notifMess;
    }
}
