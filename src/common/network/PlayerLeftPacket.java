package common.network;

import java.io.*;

public class PlayerLeftPacket extends Packet {
    public int playerId;

    public PlayerLeftPacket() {
        super(PacketType.PLAYER_LEFT);
    }

    public PlayerLeftPacket(int playerId) {
        super(PacketType.PLAYER_LEFT);
        this.playerId = playerId;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerId);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        playerId = in.readInt();
    }
}