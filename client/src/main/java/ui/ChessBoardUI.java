package ui;

import java.io.PrintStream;
import static ui.EscapeSequences.*;

public class ChessBoardUI {
    private static final String EM_SPACE = "\u2003";

    private static final String[][] INITIAL_BOARD_WHITE_BOTTOM = {
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}
    };

    private static final String[][] INITIAL_BOARD_BLACK_BOTTOM = {
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
    };

    public static void drawInitialBoards() {
        PrintStream out = System.out;
        out.print(ERASE_SCREEN);

        drawChessBoard(out, true);
        out.println();

        drawChessBoard(out, false);

        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static void drawChessBoard(PrintStream out, boolean whiteBottom) {
        String[] columns = whiteBottom ? new String[] {"a", "b", "c", "d", "e", "f", "g", "h"} :
                                         new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
        String[] rows = whiteBottom ? new String[] {"8", "7", "6", "5", "4", "3", "2", "1"} : new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[][] board = whiteBottom ? INITIAL_BOARD_WHITE_BOTTOM : INITIAL_BOARD_BLACK_BOTTOM;

        out.print("   ");
        for (String col : columns) {
            out.print(EM_SPACE + col + " ");
        }
        out.println();

        for (int i = 0; i < board.length; i++) {
            out.print(" " + rows[i] + " ");

            for (int j = 0; j < board[i].length; j++) {
                boolean isLightSquare = (i + j) % 2 == 0;
                setSquareColor(out, isLightSquare);
                out.print(" " + board[i][j] + " ");
            }

            out.print(RESET_BG_COLOR);
            out.println(" " + rows[i] + " ");
        }

        out.print("   ");
        for (String col : columns) {
            out.print(EM_SPACE + col + " ");
        }
        out.println();
    }

    private static void setSquareColor(PrintStream out, boolean isLightSquare) {
        if (isLightSquare) {
            out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
        }
    }
}