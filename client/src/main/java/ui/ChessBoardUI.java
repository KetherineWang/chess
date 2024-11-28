package ui;

import chess.ChessBoard;
import chess.ChessPiece;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class ChessBoardUI {
    private static final String EM_SPACE = "\u2003";

    public static void drawChessBoard(ChessBoard chessBoard, boolean whiteBottom) {
        PrintStream out = System.out;
        out.print(ERASE_SCREEN);

        out.println();
        drawBoard(out, chessBoard, whiteBottom);


        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static void drawBoard(PrintStream out, ChessBoard chessBoard, boolean whiteBottom) {
//        String[] columns = whiteBottom ? new String[] {"a", "b", "c", "d", "e", "f", "g", "h"} :
//                                         new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
//        String[] rows = whiteBottom ? new String[] {"8", "7", "6", "5", "4", "3", "2", "1"} :
//                                      new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] rows = new String[] {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] columns = whiteBottom ? new String[] {"a", "b", "c", "d", "e", "f", "g", "h"} : new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
        ChessPiece[][] board = chessBoard.getBoard();

        out.print("   ");
        for (String col : columns) {
            out.print(EM_SPACE + col + " ");
        }
        out.println();

        for (int i = 0; i < board.length; i++) {
            int rowIndex = whiteBottom ? 7 - i : i;
            out.print(" " + rows[rowIndex] + " ");

            for (int j = 0; j < board[rowIndex].length; j++) {
                int columnIndex = whiteBottom ? j : 7 - j;
                boolean isLightSquare = (rowIndex + columnIndex) % 2 == 0;

                setSquareColor(out, isLightSquare);

                ChessPiece piece = board[rowIndex][columnIndex];
                out.print(" " + (piece != null ? getPieceSymbol(piece) : EMPTY) + " ");
            }

            out.print(RESET_BG_COLOR);
            out.println(" " + rows[rowIndex] + " ");
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

    private static String getPieceSymbol(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_KNIGHT :BLACK_KNIGHT;
            case PAWN:
                return piece.getTeamColor() == chess.ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            default:
                return EMPTY;
        }
    }
}