package websocket.messages;

public class NotifMess {
    private final String notifMess;


    public NotifMess(String notifMess) {
        this.notifMess = notifMess;
    }

    public String getNotifMess(){
        return notifMess;
    }
}
