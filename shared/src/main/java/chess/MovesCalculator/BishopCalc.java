package chess.MovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class BishopCalc {
    private final int[][] directions = {
            {1,1}, {1,-1}, {-1, 1}, {-1,-1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] direction : directions){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true){
                row += direction[0];
                col += direction[1];

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
            }
        }
        return moves;
    }

}
