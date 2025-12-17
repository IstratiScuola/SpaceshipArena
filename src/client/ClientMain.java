package client;

import client.core.Game;

public class ClientMain {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        String playerName = "Giocatore";
        if (args.length >= 1) {
            serverAddress = args[0];
        }
        if (args.length >= 2) {
            playerName = args[1];
        }

        System.out.println("Connettendo a " + serverAddress + " come " + playerName);

        Game game = new Game(serverAddress, playerName);
        game.run();
    }
}