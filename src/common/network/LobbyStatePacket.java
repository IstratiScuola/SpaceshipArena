package common.network;

import common.GamePhase;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LobbyStatePacket extends Packet {
    public GamePhase phase;
    public List<LobbyPlayer> players;

    public LobbyStatePacket() {
        super(PacketType.LOBBY_STATE);
        players = new ArrayList<>();
    }

    public LobbyStatePacket(GamePhase phase, List<LobbyPlayer> players) {
        super(PacketType.LOBBY_STATE);
        this.phase = phase;
        this.players = players;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(phase.ordinal());
        out.writeInt(players.size());
        for (LobbyPlayer player : players) {
            out.writeInt(player.playerId);
            out.writeUTF(player.playerName);
            out.writeBoolean(player.ready);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        phase = GamePhase.values()[in.readInt()];
        int count = in.readInt();
        players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int id = in.readInt();
            String name = in.readUTF();
            boolean ready = in.readBoolean();
            players.add(new LobbyPlayer(id, name, ready));
        }
    }

    public static class LobbyPlayer implements Serializable {
        public int playerId;
        public String playerName;
        public boolean ready;

        public LobbyPlayer(int playerId, String playerName, boolean ready) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.ready = ready;
        }
    }
}
