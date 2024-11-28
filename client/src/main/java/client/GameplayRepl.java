package client;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameplayRepl implements Repl {
    private final ChessClient chessClient;

    public GameplayRepl(ChessClient chessClient) {
        this.chessClient = chessClient;
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
        if (args.length != 2) {
            return "Error: Make move requires 2 arguments: 'move <START_POSITION> <END_POSITION>'.";
        }

        try {
            String startPosition = args[0];
            String endPosition = args[1];
            return chessClient.makeMove(startPosition, endPosition);
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