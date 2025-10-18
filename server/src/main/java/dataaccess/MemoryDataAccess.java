package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authData = new HashMap<>();
    @Override
    public void clear() {
        users.clear();
        authData.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authData.get(authToken);
    }

    @Override
    public void createAuth(AuthData auth) {
        authData.put(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(String authToken) {
        authData.remove(authToken);
    }

}
