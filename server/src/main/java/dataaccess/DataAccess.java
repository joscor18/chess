package dataaccess;


import datamodel.*;

import java.util.List;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void createAuth(AuthData auth) throws DataAccessException;
    void deleteAuth(String authToken);
    GameData createGame(GameData game);
    GameData getGame(int gameId);
    void updateGame(GameData game);
    List<GameData> getGames();
}
