package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightCalc {
    private final int[][] offsets = {
            {2,-1}, {2,1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] offset : offsets){
            int row = myPosition.getRow() + offset[0];
            int col = myPosition.getColumn() + offset[1];

            if (!board.inBounds(row, col)) break;

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPosition);

            if(target == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }else{
                if(target.getTeamColor() != myPiece.getTeamColor()){
                    moves.add((new ChessMove(myPosition, newPosition, null)));
                }
                break;
            }
        }

        return moves;
    }
}
