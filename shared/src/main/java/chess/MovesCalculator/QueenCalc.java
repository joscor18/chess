package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenCalc {
    private final int[][] directions = {
            {1,1}, {1,-1}, {-1, 1}, {-1,-1}, // bishop
            {1,0}, {0,1}, {-1,0}, {0,-1} // rook
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
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

                if(target == null){ // make sure target space is empty
                    moves.add (new ChessMove(myPosition, newPosition, null));
                } else { // if not empty then stop or capture
                    if(target.getTeamColor() != myPiece.getTeamColor()){//capture opponent piece
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // stop
                    break;
                }
            }
        }
        return moves;
    }
}
