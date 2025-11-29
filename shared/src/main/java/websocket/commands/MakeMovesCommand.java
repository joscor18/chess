package websocket.commands;

import chess.ChessMove;

public class MakeMovesCommand extends UserGameCommand{
    private final ChessMove move;

    public MakeMovesCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove(){
        return move;
    }
}
