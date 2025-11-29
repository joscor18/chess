package websocket.messages;

import chess.ChessGame;

public class LoadGameMess extends ServerMessage{
    private final ChessGame game;

    public LoadGameMess(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }
}
