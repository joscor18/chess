package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.List;
import java.util.UUID;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName)throws Exception{
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new DataAccessException("unauthorized");
        }
        if(gameName == null || gameName.isBlank()){
            throw new DataAccessException("bad request");
        }
        GameData game = new GameData(0, gameName,null,null,new ChessGame());
        return dataAccess.createGame(game);
    }

    public void joinGame(String authToken, String playerColor, int gameId) throws Exception{
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new DataAccessException("unauthorized");
        }

        var game = dataAccess.getGame(gameId);
        if(game == null){
            throw new DataAccessException("bad request");
        }
        if(playerColor != null){
            if(playerColor.equalsIgnoreCase("White")){
                if(game.whiteUsername() != null){
                    throw new DataAccessException("already taken");
                }
                game = new GameData(game.gameID(), game.gameName(), authData.username(), game.blackUsername(),game.game());
            }else if(playerColor.equalsIgnoreCase("Black")){
                if(game.blackUsername() != null){
                    throw new DataAccessException("already taken");
                }
                game = new GameData(game.gameID(), game.gameName(), game.whiteUsername() ,authData.username(),game.game());
            }else {
                throw new DataAccessException("bad request");
            }
            dataAccess.updateGame(game);
        }
    }

    public List<GameData> list(String authToken) throws Exception{
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new DataAccessException("unauthorized");
        }
        return dataAccess.getGames();
    }
}
