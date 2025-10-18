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

    }
    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);

            //call to the service and register
            var authData = userService.register(user);

            //var res = Map.of("username", req.get("username"), "authToken", "yzx");
            ctx.result(serializer.toJson(authData));
        } catch (Exception ex){
            var serializer = new Gson();
            var msg = Map.of("message", "Error: " + ex.getMessage());
            ctx.status(403).result(serializer.toJson(msg));
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
