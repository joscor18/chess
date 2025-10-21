package service;

import dataaccess.DataAccess;
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
    void logoutFails() {
        DataAccess db = new MemoryDataAccess();
        var userService = new UserService(db);
        String bad = "anotherTest";
        assertNull(db.getAuth(bad));

    }
}