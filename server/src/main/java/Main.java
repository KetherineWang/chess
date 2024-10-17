import chess.*;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.ClearService;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;

            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            var dataAccess = new MemoryDataAccess();
            var clearService = new ClearService(dataAccess);
            var chessServer = new Server().run(port);

            System.out.printf("Chess Server started on port %d%n", chessServer);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start Chess Server: %s%n", ex.getMessage());
        }

        System.out.println("""
                Chess Server:
                java Main <port>
                """);
    }
}