package common.network;

import java.io.*;

public class GameOverPacket extends Packet {
    public int winnerId;
    public String winnerName;

    public GameOverPacket() {
        super(PacketType.GAME_OVER);
    }

    public GameOverPacket(int winnerId, String winnerName) {
        super(PacketType.GAME_OVER);
        this.winnerId = winnerId;
        this.winnerName = winnerName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(winnerId);
        out.writeUTF(winnerName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        winnerId = in.readInt();
        winnerName = in.readUTF();
    }
}
