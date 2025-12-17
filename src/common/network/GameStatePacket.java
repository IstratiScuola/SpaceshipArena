package common.network;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameStatePacket extends Packet {
    public List<SpaceshipState> spaceships;
    public List<BulletState> bullets;
    public long serverTick;

    public GameStatePacket() {
        super(PacketType.GAME_STATE);
        spaceships = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    public GameStatePacket(List<SpaceshipState> spaceships, 
                           List<BulletState> bullets, 
                           long serverTick) {
        super(PacketType.GAME_STATE);
        this.spaceships = spaceships;
        this.bullets = bullets;
        this.serverTick = serverTick;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(serverTick);
        

        out.writeInt(spaceships.size());
        for (SpaceshipState state : spaceships) {
            state.write(out);
        }

        out.writeInt(bullets.size());
        for (BulletState bullet : bullets) {
            bullet.write(out);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        serverTick = in.readLong();
        
        int shipCount = in.readInt();
        spaceships = new ArrayList<>();
        for (int i = 0; i < shipCount; i++) {
            SpaceshipState state = new SpaceshipState();
            state.read(in);
            spaceships.add(state);
        }
        
        int bulletCount = in.readInt();
        bullets = new ArrayList<>();
        for (int i = 0; i < bulletCount; i++) {
            BulletState bullet = new BulletState();
            bullet.read(in);
            bullets.add(bullet);
        }
    }
}