package client.network;

import common.Constants;
import common.GamePhase;
import common.network.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
//classe incaricata di gestire  la comunicazione tra client e server, gestione dello stato, gestione dei packet, trasimssione inupt etc
public class NetworkClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String serverAddress;
    private String playerName;
    private int playerId;

    private boolean connected;
    private Thread receiveThread;

    private ConcurrentLinkedQueue<Packet> incomingPackets;
    private volatile GameStatePacket lastGameState;
    private volatile LobbyStatePacket lastLobbyState;
    private volatile GamePhase currentPhase;
    private volatile GameOverPacket lastGameOver;

    public NetworkClient(String serverAddress, String playerName) {
        this.serverAddress = serverAddress;
        this.playerName = playerName;
        this.incomingPackets = new ConcurrentLinkedQueue<>();
        this.currentPhase = GamePhase.LOBBY;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverAddress, Constants.SERVER_PORT);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            JoinRequestPacket joinRequest = new JoinRequestPacket(playerName);
            Packet.writePacket(out, joinRequest);

            Packet response = Packet.readPacket(in);
            if (response.getType() == PacketType.JOIN_RESPONSE) {
                JoinResponsePacket joinResponse = (JoinResponsePacket) response;
                if (joinResponse.accepted) {
                    playerId = joinResponse.playerId;
                    connected = true;

                    System.out.println(playerId + " connesso");

                    receiveThread = new Thread(this::receiveLoop);
                    receiveThread.setDaemon(true);
                    receiveThread.start();

                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Connessione non riuscita: " + e.getMessage());
        }
        return false;
    }

    private void receiveLoop() {
        try {
            while (connected && !socket.isClosed()) {
                Packet packet = Packet.readPacket(in);
                handlePacket(packet);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("Connessione persa: " + e.getMessage());
                connected = false;
            }
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getType()) {
            case GAME_STATE:
                lastGameState = (GameStatePacket) packet;
                break;
            case LOBBY_STATE:
                lastLobbyState = (LobbyStatePacket) packet;
                currentPhase = lastLobbyState.phase;
                break;
            case GAME_START:
                currentPhase = GamePhase.PLAYING;
                lastGameOver = null;
             //   System.out.println("gioco iniziato");
                incomingPackets.add(packet);
                break;
            case GAME_OVER:
                currentPhase = GamePhase.GAME_OVER;
                lastGameOver = (GameOverPacket) packet;
             //   System.out.println("gioco finito vincitore: " + lastGameOver.winnerName);
                incomingPackets.add(packet);
                break;
            case PLAYER_JOINED:
                PlayerJoinedPacket joined = (PlayerJoinedPacket) packet;
                System.out.println("si è unito " + joined.playerName);
                incomingPackets.add(packet);
                break;
            case PLAYER_LEFT:
                PlayerLeftPacket left = (PlayerLeftPacket) packet;
                System.out.println("ci ha lasciato " + left.playerId);
                incomingPackets.add(packet);
                break;
            case PLAYER_DIED:
                PlayerDiedPacket died = (PlayerDiedPacket) packet;
                System.out.println(died.playerName + " ucciso da " + died.killerName);
                incomingPackets.add(packet);
                break;
            case PLAYER_RESPAWNED:
                incomingPackets.add(packet);
                break;
            default:
                incomingPackets.add(packet);
        }
    }

    public void sendInput(boolean rotateLeft, boolean rotateRight,
            boolean throttleUp, boolean throttleDown,
            boolean throttleMax, boolean throttleZero,
            boolean shoot) {
        if (!connected)
            return;

        try {
            InputPacket packet = new InputPacket(
                    rotateLeft, rotateRight,
                    throttleUp, throttleDown,
                    throttleMax, throttleZero,
                    shoot);
            synchronized (out) {
                Packet.writePacket(out, packet);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            disconnect();
        }
    }

    public void sendReadyToggle() {
        if (!connected)
            return;

        try {
            synchronized (out) {
                Packet.writePacket(out, new ReadyTogglePacket());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            try {
                Packet.writePacket(out, new DisconnectPacket());
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public GameStatePacket getLastGameState() {
        return lastGameState;
    }

    public LobbyStatePacket getLastLobbyState() {
        return lastLobbyState;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public GameOverPacket getLastGameOver() {
        return lastGameOver;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isConnected() {
        return connected;
    }

    public Packet pollPacket() {
        return incomingPackets.poll();
    }
}