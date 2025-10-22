package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.GameData;

import java.util.UUID;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName)throws Exception{
        AuthData authData = dataAccess.getAuth(authToken);
        if(authData == null){
            throw new Exception("unauthorized");
        }
        if(gameName == null){
            throw new Exception("bad request");
        }
        GameData game = new GameData(0, gameName,null,null,new ChessGame());
        return dataAccess.createGame(game);
    }
}
