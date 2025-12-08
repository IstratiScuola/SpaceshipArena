package client;

import client.core.Game;

public class ClientMain {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        String playerName = "Player";

        // Parse command line arguments
        if (args.length >= 1) {
            serverAddress = args[0];
        }
        if (args.length >= 2) {
            playerName = args[1];
        }

        System.out.println("Connecting to " + serverAddress + " as " + playerName);
        
        Game game = new Game(serverAddress, playerName);
        game.run();
    }
}