package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void createGameSuccess() {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        AuthData authData = new AuthData(user.username(), "testAuthToken");
        db.createAuth(authData);
        var name = "test";
        assertDoesNotThrow(() -> gameService.createGame(authData.authToken(), name));
        var games = db.getGames();
        assertEquals(1,games.size());
        assertEquals(name,games.getFirst().gameName());
    }

    @Test
    void createGameFail() {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var bad = "invalid";
        var name = "test";
        AuthData authData = new AuthData(user.username(), "token");
        db.createAuth(authData);
        var non = "";
        Exception ex = assertThrows(DataAccessException.class, ()-> gameService.createGame(bad,name));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
        Exception nonex = assertThrows(DataAccessException.class, ()-> gameService.createGame(authData.authToken(), non));
        assertTrue(nonex.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    void joinGameSuccess() throws Exception {
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        AuthData authData = new AuthData(user.username(), "testAuthToken");
        db.createAuth(authData);
        var name = "test";
        GameData game = gameService.createGame(authData.authToken(), name);
        gameService.joinGame(authData.authToken(),"White", game.gameID());
        var changed = db.getGame(game.gameID());
        assertEquals(user.username(),changed.whiteUsername());
        assertNull(changed.blackUsername());
    }

    @Test
    void joinGameFail() throws Exception{
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        AuthData authData = new AuthData(user.username(), "token");
        db.createAuth(authData);
        var name = "test";
        GameData game = gameService.createGame(authData.authToken(),name);
        var bad = "invalid";
        Exception ex = assertThrows(DataAccessException.class, ()-> gameService.joinGame(authData.authToken(),"green", game.gameID()));
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
        Exception nonex = assertThrows(DataAccessException.class, ()-> gameService.joinGame(bad,"white", game.gameID()));
        assertTrue(nonex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    void listSuccess() throws Exception{
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        AuthData authData = new AuthData(user.username(), "testAuthToken");
        db.createAuth(authData);
        gameService.createGame(authData.authToken(),"game1");
        gameService.createGame(authData.authToken(),"game2");
        var games = gameService.list(authData.authToken());
        assertEquals(2,games.size());
        assertTrue(games.stream().anyMatch(name->name.gameName().equals("game1")));
        assertTrue(games.stream().anyMatch(name->name.gameName().equals("game2")));
    }

    @Test
    void listFail() throws  Exception{
        DataAccess db = new MemoryDataAccess();
        var gameService = new GameService(db);
        Exception ex = assertThrows(DataAccessException.class, ()->{gameService.list("invalid");});
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }
}