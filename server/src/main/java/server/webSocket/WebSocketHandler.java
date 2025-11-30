package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

//    @Override
//    public void handleConnect(WsConnectContext ctx) {
//        System.out.println("Websocket connected");
//        ctx.enableAutomaticPings();
//    }

    public void handleMessage(Session session, String msg) throws IOException {
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

    private void connect(Session session, UserGameCommand cmd) throws  IOException{
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
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
