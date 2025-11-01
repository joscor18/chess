package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe","j@j.com","toomanysecrets"));
        db.clear();
        assertNull(db.getUser("joe"));
    }

    @Test
    void createUser() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        db.createUser(user);
        assertEquals(user, db.getUser(user.username()));
    }

    @Test
    void getUsersuccess() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        db.createUser(user);
        var res = db.getUser(user.username());
        assertNotNull(res);
        assertEquals(user, res);
    }

    @Test
    void getUserfail() {
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
    void getAuthfail(){
        DataAccess db = new MemoryDataAccess();
        var res = db.getAuth("non");

        assertNull(res);
    }

    @Test
    void createAuth() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
    }

    @Test
    void deleteAuth() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        AuthData authData = new AuthData("joe", "token");
        db.createAuth(authData);
        var res = db.getAuth("token");
        assertEquals(authData,res);
        db.deleteAuth(authData.authToken());
        assertNull(db.getAuth("token"));
    }

    @Test
    void createGame(){
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var res = db.getGame(1);
        assertEquals(game, res);
    }

    @Test
    void getGame(){
        DataAccess db = new MemoryDataAccess();
        var game = new GameData(1, "test", null, null, null);
        db.createGame(game);
        var res = db.getGame(1);
        assertNotNull(res);
        assertEquals(game, res);
    }

    @Test
    void updateGame(){
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
    void getGames(){
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

}