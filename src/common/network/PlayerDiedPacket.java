package common.network;

import java.io.*;

public class PlayerDiedPacket extends Packet {
    public int playerId;
    public int killerId;
    public String playerName;
    public String killerName;

    public PlayerDiedPacket() {
        super(PacketType.PLAYER_DIED);
    }

    public PlayerDiedPacket(int playerId, int killerId, 
                            String playerName, String killerName) {
        super(PacketType.PLAYER_DIED);
        this.playerId = playerId;
        this.killerId = killerId;
        this.playerName = playerName;
        this.killerName = killerName;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerId);
        out.writeInt(killerId);
        out.writeUTF(playerName);
        out.writeUTF(killerName);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        playerId = in.readInt();
        killerId = in.readInt();
        playerName = in.readUTF();
        killerName = in.readUTF();
    }
}