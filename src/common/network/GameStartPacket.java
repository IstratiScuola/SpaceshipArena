package common.network;

import java.io.*;

public class GameStartPacket extends Packet {

    public GameStartPacket() {
        super(PacketType.GAME_START);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
    }
}
