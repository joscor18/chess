package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RecurseCalc {
    private static int[][] rookDirections = {
            {1,0}, {0,1}, {-1,0}, {0,-1}
    };
    private final int[][] bishopDirections = {
            {1,1}, {1,-1}, {-1, 1}, {-1,-1} // get general directions not all steps
    };
    private static int[][] queenDirections = {
            {1,0}, {0,1}, {-1,0}, {0,-1}, {1,1}, {1,-1}, {-1, 1}, {-1,-1}
    };

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        int[][] directions;
        switch (myPiece.getPieceType()){
            case ROOK:
                directions = rookDirections;
                break;
            case BISHOP:
                directions = bishopDirections;
                break;
            case QUEEN:
                directions = queenDirections;
                break;
            default:
                return moves;
        }

        for(int[] direction : directions){
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true){
                row += direction[0];
                col += direction[1];

                if (!board.inBounds(row, col)) {
                    break; // confirm in bounds
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null){ // make sure target is empty
                    moves.add(new ChessMove(myPosition,newPosition, null));
                }else{// stop or capture
                    if (target.getTeamColor() != myPiece.getTeamColor()){
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    }
}
