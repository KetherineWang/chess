package client;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginRepl implements Repl {
    private final ChessClient chessClient;

    public PreLoginRepl(ChessClient chessClient) {
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
                case "register" -> handleRegister(args);
                case "login" -> handleLogin(args);
                case "quit" -> "quit";
                case "help" -> help();
                default -> "Error: Unrecognized command. Type 'help' for a list of commands.";
            };
        } catch (Exception ex) {
            return SET_TEXT_COLOR_RED + "An error occurred while processing the command." + RESET_TEXT_COLOR;
        }
    }

    private String handleRegister(String[] args) {
        if (args.length != 3) {
            return "Error: Register requires 3 arguments: 'register <username> <password> <email>'.";
        }
        var username = args[0];
        var password = args[1];
        var email = args[2];

        try {
            return chessClient.register(username, password, email);
        } catch (Exception ex) {
            return "Error: Unable to register. " + ex.getMessage();
        }
    }

    private String handleLogin(String[] args) {
        if (args.length != 2) {
            return "Error: Login requires 2 arguments: 'login <username> <password>'.";
        }
        var username = args[0];
        var password = args[1];

        try {
            return chessClient.login(username, password);
        } catch (Exception ex) {
            return "Error: Unable to login. " + ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                Available commands:
                register <USERNAME> <PASSWORD> <EMAIL>     - to create an account
                login <USERNAME> <PASSWORD>                - to log in
                quit                                       - to exit the application
                help                                       - to display available commands (this message)
                """;
    }

    @Override
    public void printPrompt() {
        System.out.println("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }
}