package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.List;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    @Override
    public GameData getGame(int gameId) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public List<GameData> getGames() {
        return List.of();
    }
}
