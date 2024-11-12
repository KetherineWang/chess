package client;

public class PostLoginRepl implements Repl {
    private final ChessClient chessClient;
    private final ChessApp chessApp;

    public PostLoginRepl(ChessClient chessClient, ChessApp chessApp) {
        this.chessClient = chessClient;
        this.chessApp = chessApp;
    }
}