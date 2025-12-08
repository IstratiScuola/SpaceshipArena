package common.network;

import java.io.*;

public abstract class Packet {
    protected PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return type;
    }

    public abstract void write(DataOutputStream out) throws IOException;
    public abstract void read(DataInputStream in) throws IOException;

    public static Packet readPacket(DataInputStream in) throws IOException {
        int typeOrdinal = in.readInt();
        PacketType type = PacketType.values()[typeOrdinal];

        Packet packet;
        switch (type) {
            case JOIN_REQUEST:
                packet = new JoinRequestPacket();
                break;
            case JOIN_RESPONSE:
                packet = new JoinResponsePacket();
                break;
            case INPUT:
                packet = new InputPacket();
                break;
            case GAME_STATE:
                packet = new GameStatePacket();
                break;
            case PLAYER_JOINED:
                packet = new PlayerJoinedPacket();
                break;
            case PLAYER_LEFT:
                packet = new PlayerLeftPacket();
                break;
            case PLAYER_DIED:
                packet = new PlayerDiedPacket();
                break;
            case PLAYER_RESPAWNED:
                packet = new PlayerRespawnedPacket();
                break;
            case DISCONNECT:
                packet = new DisconnectPacket();
                break;
            default:
                throw new IOException("Unknown packet type: " + type);
        }

        packet.read(in);
        return packet;
    }

    public static void writePacket(DataOutputStream out, Packet packet) throws IOException {
        out.writeInt(packet.getType().ordinal());
        packet.write(out);
        out.flush();
    }
}