package common.network;

import java.io.*;

public class ReadyTogglePacket extends Packet {

    public ReadyTogglePacket() {
        super(PacketType.READY_TOGGLE);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }
}
