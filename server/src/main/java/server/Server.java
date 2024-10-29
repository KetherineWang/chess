package server;

import service.*;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.DatabaseInitializer;
import dataaccess.DataAccessException;
import model.*;

import java.util.List;
import java.util.Map;

import spark.*;
import com.google.gson.Gson;
import exception.ResponseException;

public class Server {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ClearService clearService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListGamesService listGamesService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;

    public Server() {
        try {
            DatabaseInitializer.configureDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Unable to configure the database", ex);
        }

        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(userDAO, authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.listGamesService = new ListGamesService(authDAO, gameDAO);
        this.createGameService = new CreateGameService(authDAO, gameDAO);
        this.joinGameService = new JoinGameService(authDAO, gameDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearDatabase(Request req, Response res) throws ResponseException {
        try {
            clearService.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException ex) {
            res.status(500);
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private Object registerUser(Request req, Response res) throws ResponseException {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            if (registerRequest.username() == null || registerRequest.username().isEmpty() ||
                registerRequest.password() == null || registerRequest.password().isEmpty() ||
                registerRequest.email() == null || registerRequest.email().isEmpty()) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

            AuthData authData = registerService.register(newUser);

            RegisterResult registerResult = new RegisterResult(authData.username(), authData.authToken());
            res.status(200);
            return new Gson().toJson(registerResult);
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains("Username already exists.")) {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else {
                res.status(500);
                throw new ResponseException(500, ex.getMessage());
            }
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private Object loginUser(Request req, Response res) throws ResponseException {
        try {
            LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

            AuthData authData = loginService.login(loginRequest.username(), loginRequest.password());

            LoginResult loginResult = new LoginResult(authData.username(), authData.authToken());
            res.status(200);
            return new Gson().toJson(loginResult);
        } catch (DataAccessException ex) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private Object logoutUser(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            logoutService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (DataAccessException ex) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            List<GameData> games = listGamesService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(Map.of("games", games));
        } catch (DataAccessException ex) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            GameData gameData = createGameService.createGame(authToken, createGameRequest.gameName());

            CreateGameResult createGameResult = new CreateGameResult(gameData.gameID());
            res.status(200);
            return new Gson().toJson(createGameResult);
        } catch (DataAccessException ex) {
            if (ex.getMessage().equals("Invalid gameName")) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            } else if (ex.getMessage().equals("Invalid authToken")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            } else {
                res.status(500);
                throw new ResponseException(500, ex.getMessage());
            }
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            if (joinGameRequest.gameID() == 0 || joinGameRequest.playerColor() == null || joinGameRequest.playerColor().isEmpty()) {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }

            joinGameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            res.status(200);
            return "{}";
        } catch (DataAccessException ex) {
            if (ex.getMessage().equals("Invalid authToken")) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            } else if (ex.getMessage().contains("already taken")) {
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            } else {
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
        } catch (Exception e) {
            res.status(500);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body("{ \"message\": \"Error: " + ex.getMessage() + "\" }");
    }
}
