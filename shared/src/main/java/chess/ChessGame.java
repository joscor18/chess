package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn = TeamColor.WHITE; //white starts

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        //we start with white
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get piece
        ChessPiece myPiece = board.getPiece(startPosition);

        //check moves
        Collection<ChessMove> allMoves = myPiece.pieceMoves(board, startPosition);
        //create valid moves list
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : allMoves){
            //promote if needed
            if(myPiece.getPieceType() == ChessPiece.PieceType.PAWN && (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8)){
                board.addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece()));
            }

            //make sure king not in check



        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessBoard myBoard = getBoard();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece myPiece = myBoard.getPiece(start);

        if(myPiece == null){
            throw new InvalidMoveException("No piece at" + start);
        }

        if(isInCheck(myPiece.getTeamColor())){
            throw new InvalidMoveException("King is in Check");
        }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        //get king
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);

                if (myPiece != null && myPiece.getPieceType() == ChessPiece.PieceType.KING && myPiece.getTeamColor() == teamColor){
                    kingPosition = myPos;
                    break;
                }
            }
        }

        //other opponents
        TeamColor otherTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);

                if(myPiece != null && myPiece.getTeamColor() == otherTeam){
                    Collection<ChessMove> moves = myPiece.pieceMoves(board,myPos);

                    //do they attack king?
                    for(ChessMove move : moves){
                        if(move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //not in check
        if(!isInCheck(teamColor)){
            return false;
        }

        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++) {
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);

                //if a valid move add to list
                if(myPiece != null && myPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(myPos);

                    //make sure there are no moves in list
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
