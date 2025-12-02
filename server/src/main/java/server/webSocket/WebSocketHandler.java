package server.webSocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws IOException {
        //Action action = new Gson().fromJson(ctx.message(), Action.class);
        UserGameCommand cmd = new Gson().fromJson(msg, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            //case ENTER -> enter(action.visitorName(), ctx.session);
            case CONNECT -> connect(session, cmd);
            case MAKE_MOVE -> makeMove(session, msg);
            case LEAVE -> leave(session, cmd);
            case RESIGN -> resign(session, cmd);
        }
    }

//    @Override
//    public void handleConnect(WsConnectContext ctx) {
//        System.out.println("Websocket connected");
//        ctx.enableAutomaticPings();
//    }

    private void connect(Session session, UserGameCommand cmd) throws  IOException {
        try {
            AuthData authData = dataAccess.getAuth(cmd.getAuthToken());
            if(authData == null){
                errorMess(session, "Error: unauthorized");
                return;
            }
            GameData gameData = dataAccess.getGame(cmd.getGameID());
            if(gameData == null){
                errorMess(session, "Error: game doesn't exist");
                return;
            }
            System.out.println("DEBUG: GameObj for ID " + cmd.getGameID() + " is: " + gameData.gameID());
            System.out.println("DEBUG: gameData.game() = " + gameData.game());
            connections.add(cmd.getAuthToken(), cmd.getGameID(), session);
//        var message = String.format("%s is in the game", cmd.getAuthToken());
            LoadGameMessage loadMsg = new LoadGameMessage(gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadMsg));
//        var notification = new NotifMess(session, message);
            String msg = String.format("%s joined the game", authData.username());
            NotificationMessage notif = new NotificationMessage(msg);
            connections.broadcast(cmd.getAuthToken(), cmd.getGameID(), notif);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }



    private void resign(Session session, UserGameCommand cmd) throws IOException{
    }

    private void leave(Session session, UserGameCommand cmd) throws IOException{
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
    }

    private void makeMove(Session session, String msg) throws  IOException{
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
    }

    private void errorMess(Session session, String msg)throws IOException{
        ErrorMessage error = new ErrorMessage(msg);
        session.getRemote().sendString(new Gson().toJson(error));
    }


//    @Override
//    public void handleClose(WsCloseContext ctx) {
//        System.out.println("Websocket closed");
//    }

}
