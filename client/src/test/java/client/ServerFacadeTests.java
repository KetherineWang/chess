package client;

import server.Server;
import model.*;
import exception.ResponseException;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        try {
            facade.makeRequest("DELETE", "/db", null, Void.class);
        } catch (ResponseException ex) {
            fail("Failed to clear the database before each test: " + ex.getMessage());
        }
    }

    @Test
    void registerSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            assertNotNull(registerResult);
            assertNotNull(registerResult.authToken());
            assertTrue(registerResult.authToken().length() > 10);
        } catch (ResponseException ex) {
            fail("Expected successful registration, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void registerFailureAlreadyTaken () {
        try {
            RegisterRequest registerRequest = new RegisterRequest("duplicateUser", "password456", "duplicateUser@email.com");
            facade.register(registerRequest);

            assertThrows(ResponseException.class, () -> facade.register(registerRequest), "Expected username already taken exception.");
        } catch (ResponseException ex) {
            fail("Unexpected exception during registration failure test: " + ex.getMessage());
        }
    }

    @Test
    void loginSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("loginUser", "password789", "loginUser@email.com");
            facade.register(registerRequest);

            LoginRequest loginRequest = new LoginRequest("loginUser", "password789");
            LoginResult loginResult = facade.login(loginRequest);

            assertNotNull(loginResult);
            assertNotNull(loginResult.authToken());
            assertTrue(loginResult.authToken().length() > 10);
        } catch (ResponseException ex) {
            fail("Expected successful login, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void loginFailureInvalidCredentials() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("loginUser", "password789", "loginUser@email.com");
            facade.register(registerRequest);

            LoginRequest loginRequest = new LoginRequest("invalidUsername", "invalidPassword");
            assertThrows(ResponseException.class, () -> facade.login(loginRequest), "Expected invalid username or password exception.");
        } catch (ResponseException ex) {
            fail("Unexpected exception during login failure test: " + ex.getMessage());
        }
    }

    @Test
    void logoutSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            assertDoesNotThrow(() -> facade.logout(registerResult.authToken()));
        } catch (ResponseException ex) {
            fail("Expected successful logout, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void logoutFailureInvalidAuthToken() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            facade.register(registerRequest);

            assertThrows(ResponseException.class, () -> facade.logout("invalidAuthToken"), "Expected invalid auth token exception.");
        } catch (ResponseException ex) {
            fail("Unexpected exception during logout failure test: " + ex.getMessage());
        }
    }

    @Test
    void createGameSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            CreateGameRequest createGameRequest = new CreateGameRequest("Test Game");
            CreateGameResult createGameResult = facade.createGame(registerResult.authToken(), createGameRequest);

            assertNotNull(createGameResult);
            assertTrue(createGameResult.gameID() > 0);
        } catch (ResponseException ex) {
            fail("Expected successful game creation, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void createGameFailureInvalidAuthToken() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            facade.register(registerRequest);

            CreateGameRequest createGameRequest = new CreateGameRequest("Test Game");
            assertThrows(ResponseException.class, () ->
                    facade.createGame("invalidAuthToken", createGameRequest), "Expected invalid auth token exception."
            );
        } catch (ResponseException ex) {
            fail("Unexpected exception during game creation failure test: " + ex.getMessage());
        }
    }

    @Test
    void listGamesSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 1"));
            facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 2"));

            List<GameData> games = facade.listGames(registerResult.authToken());
            assertNotNull(games);
            assertTrue(games.size() >= 2);
        } catch (ResponseException ex) {
            fail("Expected successful game listing, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void listGamesFailureInvalidAuthToken() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 1"));
            facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 2"));

            assertThrows(ResponseException.class, () -> facade.listGames("invalidAuthToken"), "Expected invalid auth token exception.");
        } catch (ResponseException ex) {
            fail("Unexpected exception during game listing failure test: " + ex.getMessage());
        }
    }

    @Test
    void joinGameSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            CreateGameResult createGameResult = facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 3"));

            JoinGameRequest joinGameRequest = new JoinGameRequest(createGameResult.gameID(), "WHITE");
            assertDoesNotThrow(() -> facade.joinGame(registerResult.authToken(), joinGameRequest));
        } catch (ResponseException ex) {
            fail("Expected successful game join, but got exception: " + ex.getMessage());
        }
    }

    @Test
    void joinGameFailurePlayerColorAlreadyTaken() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "password123", "testUser@email.com");
            RegisterResult registerResult = facade.register(registerRequest);

            CreateGameResult createGameResult = facade.createGame(registerResult.authToken(), new CreateGameRequest("Game 3"));

            JoinGameRequest joinGameRequest = new JoinGameRequest(createGameResult.gameID(), "WHITE");
            facade.joinGame(registerResult.authToken(), joinGameRequest);

            assertThrows(ResponseException.class, () ->
                    facade.joinGame(registerResult.authToken(), joinGameRequest), "Expected player color already taken exception."
            );
        } catch (ResponseException ex) {
            fail("Unexpected exception during game join failure test: " + ex.getMessage());
        }
    }
}