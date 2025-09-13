package chess.MovesCalculator;

import chess.*;

import java.util.Collection;
import java.util.List;

public class PieceCalc {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) return List.of();

        switch (piece.getPieceType()){
            case BISHOP:
                return new BishopCalc().pieceMoves(board, myPosition);
            case KING:
                return new KingCalc().pieceMoves(board, myPosition);
            case ROOK:
                return new RookCalc().pieceMoves(board, myPosition);
            case KNIGHT:
                return new KnightCalc().pieceMoves(board, myPosition);
            case QUEEN:
                return new QueenCalc().pieceMoves(board, myPosition);
            //case PAWN:

                default:
                return List.of();
        }
    }

}
