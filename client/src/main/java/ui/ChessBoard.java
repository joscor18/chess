package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;


public class ChessBoard {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    static String drawWhite(){
        return drawBoard(true);
    }

    static String drawBlack(){
        return drawBoard(false);
    }

    private static void drawHeaders(StringBuilder out, boolean b) {
        out.append(EMPTY);

        String[] headers = b ?
                new String[]{" a ", " b ", "  c " , " d ", "  e ", "  f ", " g ", "  h "} :
                new String[]{" h ", " g ", "  f " , " e ", "  d ", "  c ", " b ", "  a "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.append(SET_BG_COLOR_BLACK);
            out.append(SET_TEXT_COLOR_WHITE);
            out.append(headers[boardCol]);
        }

        out.append(EMPTY);
        out.append("\n");
    }

//    private static void drawHeader(PrintStream out, String headerText) {
//        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
//        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
//
//        out.print(EMPTY.repeat(prefixLength));
//        printHeaderText(out, headerText);
//        out.print(EMPTY.repeat(suffixLength));
//    }
//
//    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);
//
//        out.print(player);
//
//        setBlack(out);
//    }

    private static String drawBoard(Boolean b) {
        StringBuilder out = new StringBuilder();

        String[][] board = initBoard();
        out.append("\n");
        drawHeaders(out, b);

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            int boardRow = b ? (BOARD_SIZE_IN_SQUARES - 1 - row) : row;

            drawSide(out, boardRow + 1);
            drawRowOfSquares(out, b, boardRow, board);
            drawSide(out, boardRow + 1);

            out.append("\n");
        }
        drawHeaders(out, b);
        out.append(RESET_BG_COLOR);
        return out.toString();
    }

    private static void drawSide(StringBuilder out, int i) {
        out.append(SET_BG_COLOR_BLACK);
        out.append(SET_TEXT_COLOR_WHITE);
        out.append(" ").append(i).append(" ");
    }

    private static String[][] initBoard() {
        return new String[][]{
                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
        };
    }

    private static void drawRowOfSquares(StringBuilder out, boolean b, int i, String[][] board) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            int c = b ? boardCol : (BOARD_SIZE_IN_SQUARES - 1 - boardCol);

            boolean whiteSqu = (i + c) % 2 == 0;
            if(whiteSqu){
                out.append(SET_BG_COLOR_WHITE);
            }else{
                out.append(SET_BG_COLOR_BLACK);
            }

            String piece = board[i][c];
            printPiece(out, piece);
        }

    }

    private static void printPiece(StringBuilder out, String piece) {
        if(piece.equals(WHITE_KING) || piece.equals(WHITE_QUEEN)|| piece.equals(WHITE_BISHOP)||
            piece.equals(WHITE_KNIGHT)|| piece.equals(WHITE_PAWN)|| piece.equals(WHITE_ROOK)){
            out.append(SET_TEXT_COLOR_RED);
        }else if (piece.equals(BLACK_BISHOP) || piece.equals(BLACK_KING)|| piece.equals(BLACK_KNIGHT)||
                piece.equals(BLACK_PAWN)|| piece.equals(BLACK_QUEEN)|| piece.equals(BLACK_ROOK)){
            out.append(SET_TEXT_COLOR_BLUE);
        }else{
            out.append(SET_TEXT_COLOR_BLACK);
        }

        out.append(piece);
    }

//    private static void drawHorizontalLine(PrintStream out) {
//
//        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
//                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;
//
//        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
//            setRed(out);
//            out.print(EMPTY.repeat(boardSizeInSpaces));
//
//            setBlack(out);
//            out.println();
//        }
//    }

//    private static void setWhite(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void setRed(PrintStream out) {
//        out.print(SET_BG_COLOR_RED);
//        out.print(SET_TEXT_COLOR_RED);
//    }
//
//    private static void setBlack(PrintStream out) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_BLACK);
//    }

}
