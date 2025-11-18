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

}
