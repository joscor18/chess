package dataaccess;


import datamodel.*;

import java.util.List;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    GameData createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    List<GameData> getGames() throws DataAccessException;
}
