package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalc {
    private final int[][] offsets = { //not recursive directions
            {2,-1}, {2,1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // list to collect
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] offset : offsets){ // check locations
            int row = myPosition.getRow() + offset[0];
            int col = myPosition.getColumn() + offset[1];

            if (!board.inBounds(row, col)) continue; // continue to skip those instead of exit out entirely

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPosition);

            if(target == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }else{// stop or capture
                if(target.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add((new ChessMove(myPosition, newPosition, null)));
                } // don't need to break after
            }
        }

        return moves;
    }
}
