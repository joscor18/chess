package chess.MovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // list to collect
        ChessPiece myPiece = board.getPiece(myPosition);

        //get the team color for direction
        int direction = (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1; //can always move 1 forward

        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();

        //figure out promotion
        if (board.inBounds(row, col) && board.getPiece(new ChessPosition(row, col)) == null) {
            moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        //can move 1 or 2 if first move
        //get team color for where to start
        int starting = (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        if(myPosition.getRow() == starting){
            int onemove = myPosition.getRow() + direction;
            int twomoves = myPosition.getRow() + 2*direction;
            if(board.inBounds(twomoves,col) && board.getPiece(new ChessPosition(onemove, col)) == null && board.getPiece(new ChessPosition(twomoves, col)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(twomoves, col),null));
            }
        }

        // can move diagnal if going to capture


    return moves;
    }
}
