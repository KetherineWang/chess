//import chess.*;
//
//public class Main {
//    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
//    }
//}
import client.ChessApp;

public class Main {
    public static void main(String[] args) {
        var serverURL = "http://localhost:8080";

        if(args.length == 1) {
            serverURL = args[0];
        }

        new ChessApp(serverURL).run();
    }
}