package common.network;

import java.io.*;

public class SpaceshipState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int playerId;
    public String playerName;
    public float x, y;
    public float rotation;
    public float velocityX, velocityY;
    public float throttle;
    public float[] color;
    public int health;
    public boolean alive;
    public boolean invulnerable;

    public SpaceshipState() {
        color = new float[3];
    }

    public SpaceshipState(int playerId, String playerName, float x, float y, 
                          float rotation, float velX, float velY, 
                          float throttle, float[] color, int health, 
                          boolean alive, boolean invulnerable) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.velocityX = velX;
        this.velocityY = velY;
        this.throttle = throttle;
        this.color = color.clone();
        this.health = health;
        this.alive = alive;
        this.invulnerable = invulnerable;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(playerId);
        out.writeUTF(playerName);
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(rotation);
        out.writeFloat(velocityX);
        out.writeFloat(velocityY);
        out.writeFloat(throttle);
        out.writeFloat(color[0]);
        out.writeFloat(color[1]);
        out.writeFloat(color[2]);
        out.writeInt(health);
        out.writeBoolean(alive);
        out.writeBoolean(invulnerable);
    }

    public void read(DataInputStream in) throws IOException {
        playerId = in.readInt();
        playerName = in.readUTF();
        x = in.readFloat();
        y = in.readFloat();
        rotation = in.readFloat();
        velocityX = in.readFloat();
        velocityY = in.readFloat();
        throttle = in.readFloat();
        color = new float[3];
        color[0] = in.readFloat();
        color[1] = in.readFloat();
        color[2] = in.readFloat();
        health = in.readInt();
        alive = in.readBoolean();
        invulnerable = in.readBoolean();
    }
}