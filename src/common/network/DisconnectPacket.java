package common.network;

import java.io.*;

public class DisconnectPacket extends Packet {
    public DisconnectPacket() {
        super(PacketType.DISCONNECT);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }
}