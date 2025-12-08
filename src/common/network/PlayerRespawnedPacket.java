package common.network;

import java.io.*;

public class PlayerRespawnedPacket extends Packet {
    public int playerId;

    public PlayerRespawnedPacket() {
        super(PacketType.PLAYER_RESPAWNED);
    }

    public PlayerRespawnedPacket(int playerId) {
        super(PacketType.PLAYER_RESPAWNED);
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