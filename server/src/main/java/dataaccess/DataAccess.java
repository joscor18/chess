package dataaccess;


import datamodel.*;

import java.util.HashMap;

public interface DataAccess {
    void clear();
    void createUser(UserData user) ;
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void createAuth(AuthData auth);
    void deleteAuth(String authToken);
    GameData createGame(GameData game);
    GameData getGame(int gameId);
    HashMap<Integer, GameData> listGames();
}
