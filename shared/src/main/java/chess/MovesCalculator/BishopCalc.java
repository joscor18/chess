package chess.MovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class BishopCalc {
    private final int[][] offsets = {
            {1,1}, {1,-1}, {2,2}, {2,-2}, {3,3},
            {3,-3}, {4,4}, {4,-4}, {5,5}, {5,-5},
            {6,6}, {6,-6}, {7,7}, {7,-7}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] offset : offsets){
            int newRow = myPosition.getRow() + offset[0];
            int newCol = myPosition.getColumn() + offset[1];
        }
        return moves;
    }

}
