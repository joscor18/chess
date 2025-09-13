package chess.MovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc {
    private void promotions(Collection<ChessMove> moves, ChessPosition start, ChessPosition end){
        int targetRow = end.getRow();
        if(targetRow == 8 || targetRow == 1){
            moves.add(new ChessMove(start, end,ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end,ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end,ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end,ChessPiece.PieceType.KNIGHT));
        } else{
            moves.add(new ChessMove(start, end, null));
        }
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>(); // list to collect
        ChessPiece myPiece = board.getPiece(myPosition);

        //get the team color for direction
        int direction = (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

        //can always move 1 forward
        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        if (board.inBounds(row, col) && board.getPiece(new ChessPosition(row, col)) == null) {
            promotions(moves, myPosition, new ChessPosition(row, col));
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
        int[][] offsets = {{direction,-1}, {direction,1}}; // based on color
        for (int[] offset : offsets){
            int row1 = myPosition.getRow() + offset[0];
            int col1 = myPosition.getColumn() + offset[1];
            if (board.inBounds(row1, col1)){
                ChessPiece target = board.getPiece(new ChessPosition(row1, col1));
                if(target != null && target.getTeamColor() != myPiece.getTeamColor()){
                    promotions(moves, myPosition, new ChessPosition(row1, col1));
                }
            }
        }

    return moves;
    }
}
