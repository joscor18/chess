package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalc {
    private final int [][] offsets = {
        {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}, {0,-1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // list to collect
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int[] offset: offsets){
            int row = myPosition.getRow() + offset[0];
            int col = myPosition.getColumn() + offset[1];

            if (!board.inBounds(row, col)) continue;

            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);

            if(target == null || target.getTeamColor() != myPiece.getTeamColor()){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        return moves;
    }
}
