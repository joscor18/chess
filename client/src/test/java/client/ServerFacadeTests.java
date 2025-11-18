package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDb() throws Exception{
        facade.clear();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void regPos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        Assertions.assertTrue(authData.authToken().length() > 10);
        Assertions.assertEquals("joe", authData.username());
    }

    @Test
    void regNeg() throws Exception{
        facade.register("joe", "joe", "joe");
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.register("joe", "joe", "joe");
        });
        String msg = "Error: already taken";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void loginPos() throws Exception{
        facade.register("joe", "joe", "joe");
        var authData = facade.login("joe", "joe");
        Assertions.assertTrue(authData.authToken().length() > 10);
        Assertions.assertEquals("joe", authData.username());
    }

    @Test
    void loginNeg() throws Exception{
        facade.register("joe", "joe", "joe");
        facade.login("joe", "joe");
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.login("joe", "test");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void logoutPos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        Assertions.assertDoesNotThrow(()-> facade.logout(authData.authToken()));
    }

    @Test
    void logoutNeg() throws Exception{
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.logout("joe");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void createPos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        var createGameResponse = facade.createGames("newGame", authData.authToken());
        Assertions.assertTrue(createGameResponse.gameID() > 0);
    }

    @Test
    void createNeg() throws Exception{
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.createGames("joe", "test");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void listPos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        facade.createGames("test", authData.authToken());
        var games = facade.listGames(authData.authToken());
        Assertions.assertFalse(games.isEmpty());
    }

    @Test
    void listNeg() throws Exception{
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.listGames("joe");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void joinPos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        var game = facade.createGames("test", authData.authToken());
        Assertions.assertDoesNotThrow(() -> {
            facade.joinGame(game.gameID(), "White", authData.authToken());
        });
    }

    @Test
    void joinNeg() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        var game = facade.createGames("test", authData.authToken());
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.joinGame(game.gameID(), "White", "test");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }

    @Test
    void observePos() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        var game = facade.createGames("test", authData.authToken());
        Assertions.assertDoesNotThrow(() -> {
            facade.joinGame(game.gameID(), null, authData.authToken());
        });
    }

    @Test
    void observeNeg() throws Exception{
        var authData = facade.register("joe", "joe", "joe");
        var game = facade.createGames("test", authData.authToken());
        Exception ex = Assertions.assertThrows(Exception.class, ()-> {
            facade.joinGame(game.gameID(), null, "test");
        });
        String msg = "Error: unauthorized";
        Assertions.assertTrue(ex.getMessage().contains(msg));
    }


}
