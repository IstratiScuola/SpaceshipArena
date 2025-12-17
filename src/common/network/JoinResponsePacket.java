package common.network;

import java.io.*;

public class JoinResponsePacket extends Packet {
    public int playerId;
    public boolean accepted;

    public JoinResponsePacket() {
        super(PacketType.JOIN_RESPONSE);
    }

    public JoinResponsePacket(int playerId, boolean accepted) {
        super(PacketType.JOIN_RESPONSE);
        this.playerId = playerId;
        this.accepted = accepted;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerId);
        out.writeBoolean(accepted);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        playerId = in.readInt();
        accepted = in.readBoolean();
    }
}