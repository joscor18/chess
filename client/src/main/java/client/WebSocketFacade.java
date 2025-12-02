package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.NotifMess;

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
                    NotifMess notification = new Gson().fromJson(message, NotifMess.class);
                    notificationHandler.notify(notification);
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

    public void sendCom(UserGameCommand cmd) throws IOException{
        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        }catch (IOException ex){
            throw new IOException(ex.getMessage());
        }
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

    public void leaveChess(String authToken, int gameID) throws Exception{
        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
            var cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }
    }

}
