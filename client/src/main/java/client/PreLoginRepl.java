package client;

public class PreLoginRepl implements Repl {
    private final ChessClient chessClient;
    private final ChessApp chessApp;

    public PreLoginRepl(ChessClient chessClient, ChessApp chessApp) {
        this.chessClient = chessClient;
        this.chessApp = chessApp;
    }
}