package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    private DataAccess db;
    private UserData user;
    private AuthData authData;
    private GameData game1;
    private GameData juego1;

    @BeforeEach
    void setup() throws DataAccessException{
        db = new MemoryDataAccess();
        user = new UserData("joe","j@j.com","toomanysecrets");
        authData = new AuthData("joe", "token");
        game1 = new GameData(1, "test", null, null, null);
        juego1 =  new GameData(2, "prueba", "joseph", null, null);

    }

    @Test
    void clearSuccess() throws DataAccessException {
        db.createUser(user);
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void clearFail() throws DataAccessException {
        db.createUser(new UserData("joe","j@j.com","toomanysecrets"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void createUserFail() throws DataAccessException {
        db.createUser(user);
        assertThrows(DataAccessException.class, ()->
                db.createUser(user), "Shouldn't allow duplicates");
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        db.createUser(user);
        var res = db.getUser(user.username());
        assertNotNull(res);
        assertEquals(user, res);
    }

    @Test
    void getUserFail() throws DataAccessException {
        var res = db.getUser("non");
        assertNull(res);
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertNotNull(res);
        assertEquals("joe", res.username());
        assertEquals("token",res.authToken());
    }

    @Test
    void getAuthFail() throws DataAccessException {
        var res = db.getAuth("non");

        assertNull(res);
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
    }

    @Test
    void createAuthFail() throws DataAccessException {
        db.createAuth(authData);
        assertThrows(DataAccessException.class, ()->
                db.createAuth(authData), "Shouldn't allow duplicates");
    }


    @Test
    void deleteAuthSuccess() throws DataAccessException {
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
        db.deleteAuth(authData.authToken());
        assertNull(db.getAuth("token"));
    }

    @Test
    void deleteAuthFail() throws DataAccessException {
        assertThrows(DataAccessException.class, ()->
                db.deleteAuth("non"), "missing authToken");
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        db.createGame(game1);
        var res = db.getGame(1);
        assertEquals(game1, res);
    }

    @Test
    void createGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class, ()->
                db.createGame(null), "creating null game");
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        db.createGame(game1);
        var res = db.getGame(1);
        assertNotNull(res);
        assertEquals(game1, res);
    }

    @Test
    void getGameFail() throws DataAccessException {
        db.createGame(game1);
        var res = db.getGame(4);
        assertNull(res, "nonexistent gameID");
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        db.createGame(game1);
        var changes = new GameData(1, "test", "joe", null, null);
        db.updateGame(changes);
        var res = db.getGame(1);
        assertNotNull(res);
        assertEquals(changes, res);
    }

    @Test
    void updateGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class, ()->
                db.updateGame(game1));
    }

    @Test
    void getGamesSuccess() throws DataAccessException {
        db.createGame(game1);
        db.createGame(juego1);
        var res = db.getGames();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertTrue(res.contains(game1));
        assertTrue(res.contains(juego1));
    }

    @Test
    void getGamesFail() throws DataAccessException {
        var res = db.getGames();
        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    private DataAccess createDb() throws DataAccessException{
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe","j@j.com","toomanysecrets"));
        return db;
    }

}