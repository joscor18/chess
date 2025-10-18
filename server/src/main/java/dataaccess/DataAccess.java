package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void createAuth(AuthData auth);
    void deleteAuth(String authToken);
}
