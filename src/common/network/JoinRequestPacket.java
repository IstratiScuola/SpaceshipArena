package common.network;

import java.io.*;

public class JoinRequestPacket extends Packet {
    public String playerName;

    public JoinRequestPacket() {
        super(PacketType.JOIN_REQUEST);
    }

    public JoinRequestPacket(String playerName) {
        super(PacketType.JOIN_REQUEST);
        this.playerName = playerName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(playerName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        playerName = in.readUTF();
    }
}