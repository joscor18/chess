package datamodel;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
    public String gameJSON(){
        if(game==null){
            return null;
        }
        return new Gson().toJson(game);
    }

    public static GameData fromJSON(int gameID, String gameName, String whiteUsername, String blackUsername, String gameJSON){
        ChessGame game = null;
        if(gameJSON != null && !gameJSON.isBlank()){
            game = new Gson().fromJson(gameJSON, ChessGame.class);
        }
        return new GameData(gameID, gameName, whiteUsername, blackUsername, game);
    }
}
