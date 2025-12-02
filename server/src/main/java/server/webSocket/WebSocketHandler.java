package server.webSocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMess;
import websocket.messages.NotifMess;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

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
            GameData gameData = dataAccess.getGame(cmd.getGameID());
            connections.add(cmd.getAuthToken(), cmd.getGameID(), session);
//        var message = String.format("%s is in the game", cmd.getAuthToken());
            LoadGameMess loadMsg = new LoadGameMess(gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadMsg));
//        var notification = new NotifMess(session, message);
            String msg = String.format("%s joined the game", authData.username());
            NotifMess notif = new NotifMess(msg);
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


}

//    @Override
//    public void handleClose(WsCloseContext ctx) {
//        System.out.println("Websocket closed");
//    }

}
