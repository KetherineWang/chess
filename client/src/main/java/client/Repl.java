package client;

public interface Repl {
    String help();

    String eval(String input);
}