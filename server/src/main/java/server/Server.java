package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db",ctx -> ctx.result("{}"));
        server.post("user", this::register);

        server.post("session", this:: login);
        server.delete("session", this::logout);
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
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(403).result(serializer.toJson(msg));
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
        } catch(Exception ex){
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(msg));
        }
    }

    private void logout(Context ctx){
        var serializer = new Gson();
        try{
            var authToken = ctx.header("Authorization");
            if(authToken == null){
                throw new IllegalArgumentException("unauthorized");
            }
            userService.logout(authToken);
            ctx.result("{}");
        } catch (IllegalArgumentException ex) {
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(401).result(serializer.toJson(msg));
        } catch(Exception ex){
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(400).result(serializer.toJson(msg));
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
