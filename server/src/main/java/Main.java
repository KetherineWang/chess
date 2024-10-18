import chess.*;

import service.ClearService;
import service.RegisterService;
import server.Server;
import dataaccess.MemoryDataAccess;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;

            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

//            var dataAccess = new MemoryDataAccess();
//            var clearService = new ClearService(dataAccess);
//            var registerService = new RegisterService(dataAccess);
            var chessServer = new Server().run(port);

            System.out.printf("Chess Server started on port %d%n", chessServer);
            return;
        } catch (Throwable e) {
            System.out.printf("Unable to start Chess Server: %s%n", e.getMessage());
        }

        System.out.println("""
                Chess Server:
                java Main <port>
                """);
    }
}