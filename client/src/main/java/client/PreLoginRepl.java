package client;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginRepl implements Repl {
    private final ChessClient chessClient;
    private final ChessApp chessApp;

    public PreLoginRepl(ChessClient chessClient, ChessApp chessApp) {
        this.chessClient = chessClient;
        this.chessApp = chessApp;
    }

    @Override
    public String eval(String input) {
        var tokens = input.split("\\s+");
        if (tokens.length == 0) {
            return "Error: No command entered.";
        }

        var command = tokens[0].toLowerCase();
        var args = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (command) {
            case "help" -> help();
            case "register" -> handleRegister(args);
            case "login" -> handleLogin(args);
            case "quit" -> "quit";
            default -> "Error: Unrecognized command. Type 'help' for a list of commands.";
        };
    }

    private String handleRegister(String[] args) {
        if (args.length != 3) {
            return "Error: Register requires 'register <username> <password> <email>'.";
        }

        try {
            var response = chessClient.register(args[0], args[1], args[2]);
            chessApp.switchToPostLogin();
            return response;
        } catch (Exception ex) {
            return "Error: Unable to register. " + ex.getMessage();
        }
    }

    private String handleLogin(String[] args) {
        if (args.length != 2) {
            return "Error: Login requires <username> <password>";
        }

        try {
            var response = chessClient.login(args[0], args[1]);
            chessApp.switchToPreLogin();
            return response;
        } catch (Exception ex) {
            return "Error: Unable to login. " + ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                Available commands:
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to log in
                quit - to exit the application
                help - to display available commands (this message)
                """;
    }

    @Override
    public void printPrompt() {
        System.out.println("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}