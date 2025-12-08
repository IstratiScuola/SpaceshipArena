package server;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println("Starting Spaceship Server...");
        GameServer server = new GameServer();
        server.start();
    }
}