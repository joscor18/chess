package server.webSocket;

import org.eclipse.jetty.websocket.api.Session;

public class Connect {
    public String authToken;
    public Integer gameID;
    public Session session;

    public Connect(String authToken, Integer gameID, Session session){
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }
}
