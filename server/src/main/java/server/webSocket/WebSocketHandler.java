package server.webSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMovesCommand;
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
        try {
            MakeMovesCommand movesCommand = new Gson().fromJson(msg, MakeMovesCommand.class);
            AuthData authData = dataAccess.getAuth(movesCommand.getAuthToken());
            if(authData == null){
                errorMess(session, "Error: unauthorized");
            }
            GameData gameData = dataAccess.getGame(movesCommand.getGameID());
            ChessGame game = gameData.game();

            game.makeMove(movesCommand.getMove());

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.broadcast("", movesCommand.getGameID(), loadGameMessage);

            String notif = String.format("%s made a move: %s", authData.username(), movesCommand.getMove().toString());
            NotificationMessage notificationMessage = new NotificationMessage(notif);
            connections.broadcast(authData.authToken(), movesCommand.getGameID(), notificationMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
