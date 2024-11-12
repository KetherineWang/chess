package client;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PostLoginRepl implements Repl {
    private final ChessClient chessClient;
    private final String authToken;

    public PostLoginRepl(ChessClient chessClient, String authToken) {
        this.chessClient = chessClient;
        this.authToken = authToken;
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
                case "create" -> handleCreateGame(args);
                case "list" -> handleListGames();
                case "join" -> handleJoinGame(args);
                case "observe" -> handleObserveGame(args);
                case "logout" -> handleLogout();
                case "quit" -> "quit";
                case "help" -> help();
                default -> "Error: Unrecognized command. Type 'help' for a list of commands.";
            };
        } catch (Exception ex) {
            return SET_TEXT_COLOR_RED + "An error occurred while processing the command." + RESET_TEXT_COLOR;
        }
    }

    private String handleCreateGame(String[] args) {
        if (args.length != 1) {
            return "Error: Create game requires 1 argument: 'create <GAME NAME>'.";
        }
        var gameName = args[0];

        try {
            return chessClient.createGame(gameName);
        } catch (Exception ex) {
            return "Error: Unable to create game. " + ex.getMessage();
        }
    }

    private String handleListGames() {
        try {
            return chessClient.listGames();
        } catch (Exception ex) {
            return "Error: Unable to list games. " + ex.getMessage();
        }
    }

    private String handleJoinGame(String[] args) {
        if (args.length != 2) {
            return "Error: Join game requires 2 arguments: 'join <GAME ID> <PLAYER COLOR: WHITE|BLACK>'.";
        }

        try {
            int gameID = Integer.parseInt(args[0]);
            String playerColor = args[1].toUpperCase();
            return chessClient.joinGame(gameID, playerColor);
        } catch (NumberFormatException ex) {
            return "Error: Game ID must be an integer.";
        } catch (Exception e) {
            return "Error: Unable to join game. " + e.getMessage();
        }
    }

    private String handleObserveGame(String[] args) {
        if (args.length != 1) {
            return "Error: Observe game requires 1 argument: 'observe <GAME ID>'.";
        }

        try {
            int gameID = Integer.parseInt(args[0]);
            return chessClient.observeGame(gameID);
        } catch (NumberFormatException ex) {
            return "Error: Game ID must be an integer.";
        } catch (Exception e) {
            return "Error: Unable to observe game. " + e.getMessage();
        }
    }

    private String handleLogout() {
        try {
            return chessClient.logout();
        } catch (Exception ex) {
            return "Error: Unable to logout. " + ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                Available commands:
                logout                                         - to log out
                create <GAME NAME>                             - to create a game
                list                                           - to list games
                join <GAME ID> <PLAYER COLOR: WHITE|BLACK>     - to join a game
                observe <GAME ID>                              - to observe a game
                quit                                           - to exit the application
                help                                           - to display available commands (this message)
                """;
    }

    @Override
    public void printPrompt() {
        System.out.println("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
    }
}