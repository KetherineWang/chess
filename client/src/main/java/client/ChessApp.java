package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessApp {
    private Repl currentRepl;
    private final ChessClient chessClient;
    private final PreLoginRepl preLoginRepl;
    private PostLoginRepl postLoginRepl;
    private GameplayRepl gameplayRepl;

    public ChessApp(String serverURL) {
        this.chessClient = new ChessClient(serverURL, this);
        this.preLoginRepl = new PreLoginRepl(chessClient);
        this.currentRepl = preLoginRepl;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(WHITE_KING + " Welcome to Chess 240. Type 'help' to get started. " + BLACK_KING);

        while (true) {
            currentRepl.printPrompt();
            var line = scanner.nextLine().trim();

            var result = currentRepl.eval(line);
            System.out.println(SET_TEXT_COLOR_BLUE + result + RESET_TEXT_COLOR);

            if (line.startsWith("quit")) {
                System.out.println("Exiting Chess 240. Goodbye!");
                break;
            }
        }
    }

    public void switchToPostLogin() {
        this.postLoginRepl = new PostLoginRepl(chessClient);
        this.currentRepl = postLoginRepl;
    }

    public void switchToPreLogin() {
        this.currentRepl = preLoginRepl;
    }

    public void switchToGameplay() {
        this.gameplayRepl = new GameplayRepl(chessClient);
        this.currentRepl = gameplayRepl;
    }
}