package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.MakeMovesCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotifHandler notificationHandler;

    public WebSocketFacade(String url, NotifHandler notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> notificationHandler.notify(new Gson().fromJson(message, LoadGameMessage.class));
                        case ERROR -> notificationHandler.notify(new Gson().fromJson(message, ErrorMessage.class));
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


    public void connectChess(String authToken, int gameID) throws Exception{
        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
            var cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception{
        try {
            var cmd = new MakeMovesCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException e) {
            throw new Exception("Error sending move: " + e.getMessage());
        }
    }


    public void leaveChess(String authToken, int gameID) throws Exception{
        try {
            var cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void resignChess(String authToken, int gameID) throws Exception{
        try {
            var cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

}
