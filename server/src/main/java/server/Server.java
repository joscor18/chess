package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import server.webSocket.WebSocketHandler;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public Server() {
        // try catch needed for SqlDataAccess Exception to not break everything else
        try {
            dataAccess = new SqlDataAccess();
            userService = new UserService(dataAccess);
            gameService = new GameService(dataAccess);
            WebSocketHandler wsHandler = new WebSocketHandler(dataAccess);

            server = Javalin.create(config -> config.staticFiles.add("web"));
            server.ws("/ws", ws -> {
                ws.onMessage(ctx -> {
                    wsHandler.onMessage(ctx.session, ctx.message())
                ;});
            });

            server.exception(Exception.class, (ex, ctx)->{
                ctx.status(500);
                var msg = Map.of("message", "Error: "+ex.getMessage());
                ctx.result(gson.toJson(msg));
            });

            // Register your endpoints and exception handlers here.
            server.delete("db",ctx -> {dataAccess.clear(); ctx.result("{}");});
            server.post("user", this::register);

            server.post("session", this:: login);
            server.delete("session", this::logout);

            server.post("game", this::createGame);
            server.put("game", this::joinGame);
            server.get("game",this::list);
        }catch (DataAccessException ex){
            throw new RuntimeException("Database failed",ex);
        }

    }
    private void register(Context ctx){
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            if(user.username() == null || user.password() == null || user.email() == null){
                var msg = Map.of("message", "Error: bad request");
                ctx.status(400).result(serializer.toJson(msg));
                return;
            }

            //call to the service and register
            var authData = userService.register(user);

            //var res = Map.of("username", req.get("username"), "authToken", "yzx");
            ctx.result(serializer.toJson(authData));
        } catch (Exception ex){
            evalMess(ex, ctx, serializer);
        }
    }

    private void login(Context ctx){
        var serializer = new Gson();
        try{
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            if(user.username() == null || user.password() == null){
                throw new IllegalArgumentException("bad request");
            }
            //Check user
            var existUser = userService.login(user);

            ctx.result(serializer.toJson(existUser));
        } catch (IllegalArgumentException ex) {
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(msg));
        } catch(Exception ex) {
            evalMess(ex, ctx, serializer);
        }
    }

    private void logout(Context ctx){
        var serializer = new Gson();
        try{
            var authToken = ctx.header("Authorization");
            userService.logout(authToken);
            ctx.result("{}");
        } catch (Exception ex) {
            evalMess(ex, ctx, serializer);
        }
    }

    private void list(Context ctx){
        var serializer = new Gson();
        try{
            var authToken = ctx.header("Authorization");
            var games = gameService.list(authToken);
            var req = Map.of("games", games);
            ctx.result(serializer.toJson(req));

        } catch (Exception ex) {
            evalMess(ex, ctx, serializer);
        }
    }

    private void createGame(Context ctx){
        var serializer = new Gson();
        try{
            var authToken = ctx.header("Authorization");

            String reqJson = ctx.body();

            var game = serializer.fromJson(reqJson, GameData.class);

            var existGame = gameService.createGame(authToken, game.gameName());

            ctx.result(serializer.toJson(existGame));

        } catch (Exception ex) {
            evalMess(ex, ctx, serializer);
        }
    }

    private void joinGame(Context ctx){
        var serializer = new Gson();
        try{
            var authToken = ctx.header("Authorization");

            String reqJson = ctx.body();
            var joinreq = serializer.fromJson(reqJson, Map.class);
            if(joinreq == null || !joinreq.containsKey("gameID")){
                var msg = Map.of("message", "Error: bad request");
                ctx.status(400).result(serializer.toJson(msg));
                return;
            }

            var playerColor = joinreq.get("playerColor");
            var stringPlayerColor = (String)playerColor;
            if(stringPlayerColor == null){
                var msg = Map.of("message", "Error: bad request");
                ctx.status(400).result(serializer.toJson(msg));
                return;
            }
            var gameId = joinreq.get("gameID");
            int gameIdCast = ((Number)gameId).intValue();
            gameService.joinGame(authToken, stringPlayerColor, gameIdCast);
            ctx.result("{}");
        } catch (Exception ex) {
            evalMess(ex, ctx, serializer);
        }
    }

    //opens up the port we want
    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    //Makes sure that we don't continue to open http handlers
    public void stop() {
        server.stop();
    }

    //Checks all message possibilities for simplicity
    private void evalMess(Exception ex, Context ctx, Gson serializer){
        var msg = Map.of("message", "Error: " + ex.getMessage());
        String mess = ex.getMessage().toLowerCase();
        if(mess.contains("unauthorized")){
            ctx.status(401).result(serializer.toJson(msg));
        }else if (mess.contains("bad request")){
            ctx.status(400).result(serializer.toJson(msg));
        }else if (mess.contains("already taken")) {
            ctx.status(403).result(serializer.toJson(msg));
        }else{
            ctx.status(500).result(serializer.toJson(msg));
        }
    }
}
