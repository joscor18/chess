package client;

import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotifHandler {
    void notify(ServerMessage notifMess);
}
