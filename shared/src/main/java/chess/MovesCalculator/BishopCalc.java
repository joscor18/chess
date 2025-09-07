package chess.MovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class BishopCalc {
    private final int[][] directions = {
            {1,1}, {1,-1}, {-1, 1}, {-1,-1} // get general directions not all steps
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

                if (!board.inBounds(row, col)) break; // make sure in bounds

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);

                if(target == null){// make sure target space is empty
                    moves.add (new ChessMove(myPosition, newPosition, null));
                } else { // if not empty then stop
                    break;
                }
            }
        }
        return moves;
    }

}
