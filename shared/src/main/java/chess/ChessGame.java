package chess;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        if(myPiece == null){
            return new ArrayList<>();
        }

        //check moves
        Collection<ChessMove> allMoves = myPiece.pieceMoves(board, startPosition);
        //create valid moves list
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : allMoves){
            ChessBoard boardCopy = new ChessBoard();

            for(int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition myPos = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(myPos);
                    if (piece != null) {
                        boardCopy.addPiece(myPos, new ChessPiece(piece.getTeamColor(), piece.getPieceType()));
                    }
                }
            }


            //promote if needed
            if(myPiece.getPieceType() == ChessPiece.PieceType.PAWN && (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8)){
                boardCopy.addPiece(move.getStartPosition(), null);
                boardCopy.addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece()));
            }else{
                boardCopy.addPiece(move.getStartPosition(), null);
                boardCopy.addPiece(move.getEndPosition(), new ChessPiece(myPiece.getTeamColor(),myPiece.getPieceType()));
            }

            //make sure king not in check
            ChessGame test = new ChessGame();
            test.board = boardCopy;
            test.setBoard(boardCopy);
            if(!test.isInCheck(myPiece.getTeamColor())){
                validMoves.add(move);
            }
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
            throw new InvalidMoveException("No piece at " + start);
        }

        if(myPiece.getTeamColor() != currentTurn){
            throw new InvalidMoveException("It's not this teams turn");
        }

        if(isInCheck(myPiece.getTeamColor())){
            throw new InvalidMoveException("King is in Check");
        }

        Collection<ChessMove> moves = validMoves(start);
        if(!moves.contains(move)){
            throw new InvalidMoveException("Not Valid Move " + move);
        }

        ChessPiece isCaptured = myBoard.getPiece(end);

        myBoard.addPiece(start, null);

        if(myPiece.getPieceType() == ChessPiece.PieceType.PAWN && (end.getRow() == 1 || end.getRow() == 8)){
            ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
            if(promotionPiece == null){
                promotionPiece = ChessPiece.PieceType.QUEEN;
            }
            myBoard.addPiece(end, new ChessPiece(myPiece.getTeamColor(),promotionPiece));
        }else{
            myBoard.addPiece(end,myPiece);
        }


        if(isInCheck(myPiece.getTeamColor())){
            myBoard.addPiece(start,myPiece);
            if(isCaptured != null){ myBoard.addPiece(end, isCaptured);}
            throw new InvalidMoveException("Makes king in check");
        }

        //change team turn
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKing(teamColor);
        TeamColor otherTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        //get king
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);

                if(myPiece == null || myPiece.getTeamColor() != otherTeam){
                    continue;
                }
                if(attackKing(myPiece,myPos,kingPosition)){
                    return true;
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
        return isInCheck(teamColor) && !checkValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !checkValidMoves(teamColor);
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", currentTurn=" + currentTurn +
                '}';
    }

    private boolean checkValidMoves(TeamColor teamColor){
        for(int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);
                if (myPiece != null && myPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(myPos);
                    if (!moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean attackKing(ChessPiece piece, ChessPosition start, ChessPosition kingPos){
        for(ChessMove move : piece.pieceMoves(board,start)){
            if(move.getEndPosition().equals(kingPos)){
                return true;
            }
        }
        return false;
    }

    private ChessPosition getKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition myPos = new ChessPosition(row, col);
                ChessPiece myPiece = board.getPiece(myPos);
                if(myPiece != null && myPiece.getTeamColor() == teamColor && myPiece.getPieceType() == ChessPiece.PieceType.KING){
                    return myPos;
                }

            }
        }
        return null;
    }
}
