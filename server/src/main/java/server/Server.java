package server;

import service.ClearService;
import service.RegisterService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;

import com.google.gson.Gson;
import exception.ResponseException;
import spark.*;

public class Server {
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final RegisterService registerService;

    public Server() {
        this.dataAccess = new MemoryDataAccess();
        this.clearService = new ClearService(dataAccess);
        this.registerService = new RegisterService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);

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
            res.status(403);
            return "{ \"message\": \"Error: already taken\" }";
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
