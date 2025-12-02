package server.webSocket;

import com.google.gson.Gson;
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

    public void broadcast(String authToken, Integer gameID, ServerMessage notification) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(gameID) && !c.authToken.equals(authToken)) {
                    c.send(new Gson().toJson(notification));
                }
            }
        }
    }
}
