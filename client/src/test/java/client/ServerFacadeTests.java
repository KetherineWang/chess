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
}
