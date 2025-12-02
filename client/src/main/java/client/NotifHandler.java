package client;

import websocket.messages.NotifMess;

public interface NotifHandler {
    void notify(NotifMess notifMess);
}
