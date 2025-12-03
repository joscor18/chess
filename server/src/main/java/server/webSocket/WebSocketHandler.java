package server.webSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
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
        try {
            AuthData authData = dataAccess.getAuth(cmd.getAuthToken());
            if(authData == null){
                errorMess(session, "Error: unauthorized");
                return;
            }

            connections.remove(cmd.getAuthToken());

            String notif = String.format("%s left the game", authData.username());
            NotificationMessage notificationMessage = new NotificationMessage(notif);
            connections.broadcast(authData.authToken(), cmd.getGameID(), notificationMessage);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void makeMove(Session session, String msg) throws  IOException{
        try {
            MakeMovesCommand movesCommand = new Gson().fromJson(msg, MakeMovesCommand.class);
            AuthData authData = dataAccess.getAuth(movesCommand.getAuthToken());
            if(authData == null){
                errorMess(session, "Error: unauthorized");
                return;
            }
            GameData gameData = dataAccess.getGame(movesCommand.getGameID());
            if(gameData == null){
                errorMess(session, "Error: game doesn't exist");
                return;
            }
            ChessGame game = gameData.game();

            if((game.getTeamTurn() == ChessGame.TeamColor.WHITE &&
                    !authData.username().equals(gameData.whiteUsername())) ||
                    (game.getTeamTurn() == ChessGame.TeamColor.BLACK &&
                    !authData.username().equals(gameData.blackUsername()))){
                errorMess(session, "It is not your turn");
                return;
            }

            game.makeMove(movesCommand.getMove());

            GameData updateGame = new GameData(gameData.gameID(),
                    gameData.gameName(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    game);
            dataAccess.updateGame(updateGame);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.broadcast("", movesCommand.getGameID(), loadGameMessage);

            String notif = String.format("%s made a move: %s", authData.username(), movesCommand.getMove().toString());
            NotificationMessage notificationMessage = new NotificationMessage(notif);
            connections.broadcast(authData.authToken(), movesCommand.getGameID(), notificationMessage);
        } catch (InvalidMoveException ex){
            errorMess(session, "Invalid move" + ex.getMessage());
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
