package server;

import common.network.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private DataInputStream in;
    private DataOutputStream out;

    private int playerId;
    private String playerName;
    private boolean connected;
    private boolean ready;

    private volatile InputPacket lastInput;

    public ClientHandler(Socket socket, GameServer server, int playerId) {
        this.socket = socket;
        this.server = server;
        this.playerId = playerId;
        this.connected = false;
        this.ready = false;
        this.lastInput = new InputPacket(false, false, false, false, false, false, false);
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            Packet packet = Packet.readPacket(in);
            if (packet.getType() == PacketType.JOIN_REQUEST) {
                JoinRequestPacket joinRequest = (JoinRequestPacket) packet;
                playerName = joinRequest.playerName;

                JoinResponsePacket response = new JoinResponsePacket(playerId, true);
                Packet.writePacket(out, response);

                connected = true;
                server.onPlayerJoined(this);

                System.out.println("Player " + playerName + " (ID: " + playerId + ") connected");

                while (connected && !socket.isClosed()) {
                    packet = Packet.readPacket(in);
                    handlePacket(packet);
                }
            }
        } catch (IOException e) {
            System.out.println("Player " + playerName + " disconnected: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getType()) {
            case INPUT:
                lastInput = (InputPacket) packet;
                break;
            case READY_TOGGLE:
                ready = !ready;
                server.onPlayerReadyChanged(this);
                break;
            case DISCONNECT:
                connected = false;
                break;
            default:
                System.out.println("Unknown packet type from client: " + packet.getType());
        }
    }

    public void sendPacket(Packet packet) {
        try {
            synchronized (out) {
                Packet.writePacket(out, packet);
            }
        } catch (IOException e) {
            System.out.println("Failed to send packet to " + playerName + ": " + e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            server.onPlayerLeft(this);
            System.out.println("Player " + playerName + " (ID: " + playerId + ") left");
        }
        try {
            socket.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public InputPacket getLastInput() {
        return lastInput;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}