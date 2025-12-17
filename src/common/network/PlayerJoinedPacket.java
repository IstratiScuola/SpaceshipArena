package common.network;

import java.io.*;

public class PlayerJoinedPacket extends Packet {
    public int playerId;
    public String playerName;

    public PlayerJoinedPacket() {
        super(PacketType.PLAYER_JOINED);
    }

    public PlayerJoinedPacket(int playerId, String playerName) {
        super(PacketType.PLAYER_JOINED);
        this.playerId = playerId;
        this.playerName = playerName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerId);
        out.writeUTF(playerName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        playerId = in.readInt();
        playerName = in.readUTF();
    }
}