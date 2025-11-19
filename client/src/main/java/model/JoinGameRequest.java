package model;

public class JoinGameRequest {
    public String playerColor;
    public int gameID;

    public JoinGameRequest(String playerColor, int gameID){
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public JoinGameRequest(int gameID){
        this.gameID = gameID;
    }
}
