package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register()throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData("joe","j@j.com","toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void registerInvalidUsername()throws Exception {
        DataAccess db = new MemoryDataAccess();
        var user = new UserData(null,"j@j.com","toomanysecrets");
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void loginSuccess()throws Exception{
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        userService.register(user);
        AuthData authData = userService.login(user);
        var res = db.getAuth(authData.authToken());
        assertNotNull(authData);
        assertEquals("joe",authData.username());
        assertNotNull(authData.authToken());
        assertEquals(authData,res);
    }

    @Test
    void loginFails() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        var user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        var bad = new UserData("moe", null, "wrong");
        assertThrows(DataAccessException.class, ()->{userService.login(bad);});

    }


    @Test
    void logoutSuccess()throws Exception{
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        AuthData authData = new AuthData("username", "testAuthToken");
        db.createAuth(authData);
        assertNotNull(authData);
        assertDoesNotThrow(() -> {userService.logout(authData.authToken());});
        assertNull(db.getAuth(authData.authToken()));
    }

    @Test
    void logoutFails() throws DataAccessException {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        AuthData authData = new AuthData("user", "token");
        db.createAuth(authData);
        String bad = "anotherTest";
        assertThrows(DataAccessException.class, ()->userService.logout(bad));
        assertNotNull(db.getAuth(authData.authToken()));

    }
}