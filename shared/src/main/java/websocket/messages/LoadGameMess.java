package websocket.messages;

import chess.ChessGame;

public class LoadGameMess extends ServerMessage{
    private final ChessGame game;

    public LoadGameMess(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame(){
        return game;
    }
}
