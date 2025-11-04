package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clearSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe","j@j.com","toomanysecrets"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void clearFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe","j@j.com","toomanysecrets"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void createUserFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        db.createUser(user);
        assertThrows(DataAccessException.class, ()->
                db.createUser(user), "Shouldn't allow duplicates");
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        db.createUser(user);
        var res = db.getUser(user.username());
        assertNotNull(res);
        assertEquals(user, res);
    }

    @Test
    void getUserFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var res = db.getUser("non");
        assertNull(res);
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertNotNull(res);
        assertEquals("joe", res.username());
        assertEquals("token",res.authToken());
    }

    @Test
    void getAuthFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var res = db.getAuth("non");

        assertNull(res);
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
    }

    @Test
    void createAuthFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        assertThrows(DataAccessException.class, ()->
                db.createAuth(authData), "Shouldn't allow duplicates");
    }


    @Test
    void deleteAuthSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
        db.deleteAuth(authData.authToken());
        assertNull(db.getAuth("token"));
    }

    @Test
    void deleteAuthFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        assertThrows(DataAccessException.class, ()->
                db.deleteAuth("non"), "missing authToken");
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var res = db.getGame(1);
        assertEquals(game, res);
    }

    @Test
    void createGameFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        assertThrows(DataAccessException.class, ()->
                db.createGame(null), "creating null game");
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var res = db.getGame(1);
        assertNotNull(res);
        assertEquals(game, res);
    }

    @Test
    void getGameFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var res = db.getGame(4);
        assertNull(res, "nonexistent gameID");
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var changes = new GameData(1, "test", "joe", null, null);
        db.updateGame(changes);
        var res = db.getGame(1);
        assertNotNull(res);
        assertEquals(changes, res);
    }

    @Test
    void updateGameFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "non", null, null, null);
        assertThrows(DataAccessException.class, ()->
                db.updateGame(game));
    }

    @Test
    void getGamesSuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        var juego = new GameData(2, "prueba", "joseph", null, null);
        db.createGame(game);
        db.createGame(juego);
        var res = db.getGames();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertTrue(res.contains(game));
        assertTrue(res.contains(juego));
    }

    @Test
    void getGamesFail() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        var juego = new GameData(2, "prueba", "joseph", null, null);
        var res = db.getGames();
        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

}