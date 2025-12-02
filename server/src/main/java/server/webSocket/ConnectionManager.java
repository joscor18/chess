package server.webSocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connect> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connect(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String authToken, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(authToken)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
