package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameplayRepl implements Repl {
    private final ChessClient chessClient;
    private final int gameID;

    public GameplayRepl(ChessClient chessClient, int gameID) {
        this.chessClient = chessClient;
        this.gameID = gameID;
    }

    @Override
    public String eval(String input) {
        var tokens = input.split("\\s+");
        if (tokens.length == 0) {
            return "Error: No command entered.";
        }

        var command = tokens[0].toLowerCase();
        var args = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            return switch (command) {
                case "move" -> handleMove(args);
                case "leave" -> handleLeave();
                case "resign" -> handleResign();
                case "quit" -> "quit";
                case "help" -> help();
                default -> "Error: Unrecognized command. Type 'help' for a list of commands.";
            };
        } catch (Exception ex) {
            return SET_TEXT_COLOR_RED + "An error occurred while processing the command." + RESET_TEXT_COLOR;
        }
    }

    private String handleMove(String[] args) {
        if (args.length < 2 || args.length > 3) {
            return "Error: Make move requires 2 or 3 arguments: 'move <START_POSITION> <END_POSITION> [PROMOTION_PIECE]'.";
        }

        try {
            ChessPosition startPosition = parsePosition(args[0]);
            ChessPosition endPosition = parsePosition(args[1]);

            ChessPiece.PieceType promotionPiece = null;
            if (args.length == 3) {
                promotionPiece = parsePromotionPiece(args[2]);
            }

            ChessMove chessMove = new ChessMove(startPosition, endPosition, promotionPiece);

            return chessClient.makeMove(gameID, chessMove);
        } catch (IllegalArgumentException ex) {
            return "Error: " + ex.getMessage();
        } catch (Exception ex) {
            return "Error: Unable to make move. " + ex.getMessage();
        }
    }

    private String handleLeave() {
        try {
            return chessClient.leaveGame();
        } catch (Exception ex) {
            return "Error: Unable to leave game. " + ex.getMessage();
        }
    }

    private String handleResign() {
        try {
            return chessClient.resignGame();
        } catch (Exception ex) {
            return "Error: Unable to resign." + ex.getMessage();
        }
    }

    private ChessPosition parsePosition(String positionInput) {
        if (positionInput.length() != 2) {
            throw new IllegalArgumentException("Invalid position input format. Use a column (a-h) followed by a row (1-8).");
        }

        char columnChar = positionInput.charAt(0);
        char rowChar = positionInput.charAt(1);

        int column = columnChar - 'a' + 1;
        if (column < 1 || column > 8) {
            throw new IllegalArgumentException("Invalid column. Use a letter between 'a' and 'h',");
        }

        int row = rowChar - '1' + 1;
        if (row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid row. Use a number between '1' and '8'.");
        }

        return new ChessPosition(row, column);
    }

    private ChessPiece.PieceType parsePromotionPiece(String promotionPiece) {
        switch (promotionPiece.toLowerCase()) {
            case "queen":
                return ChessPiece.PieceType.QUEEN;
            case "bishop":
                return ChessPiece.PieceType.BISHOP;
            case "knight":
                return ChessPiece.PieceType.KNIGHT;
            case "rook":
                return ChessPiece.PieceType.ROOK;
            default:
                throw new IllegalArgumentException("Invalid promotion piece. Valid options are: queen, bishop, knight, rook.");
        }
    }

    @Override
    public String help() {
        return """
               Available commands:
               move <START_POSITION> <END_POSITION>     - to make a move
               leave                                    - to leave the game
               resign                                   - to resign from the game
               quit                                     - to exit the application
               help                                     - to display available commands (this message)
               """;
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN + RESET_TEXT_COLOR);
    }
}