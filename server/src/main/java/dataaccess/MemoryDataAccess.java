package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authData = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();

    private int nextGameId =1;

    @Override
    public void clear() {
        users.clear();
        authData.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if(users.containsKey(user.username())){
            throw new DataAccessException("User exists: "+ user.username());
        }
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
    public void createAuth(AuthData auth) throws DataAccessException {
        if(authData.containsKey(auth.authToken())){
            throw new DataAccessException("Auth token exists: "+ auth.authToken());
        }
        authData.put(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if(!authData.containsKey(authToken)){
            throw new DataAccessException("npo auth found for authToken: " + authToken);
        }
        authData.remove(authToken);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException{
        if(game == null || game.gameName() == null){
            throw new DataAccessException("Invalid game");
        }
        int id = nextGameId++;
        GameData newGame = new GameData(id,game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
        games.put(id, newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameId) {
        return games.get(gameId);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{
        if(!games.containsKey(game.gameID())){
            throw new DataAccessException("no game found for: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }

    @Override
    public List<GameData> getGames() {
        return new ArrayList<>(games.values());
    }


}
