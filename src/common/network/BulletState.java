package common.network;

import java.io.*;

public class BulletState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int bulletId;
    public int ownerId;
    public float x, y;
    public float velocityX, velocityY;

    public BulletState() {}

    public BulletState(int bulletId, int ownerId, float x, float y, 
                       float velocityX, float velocityY) {
        this.bulletId = bulletId;
        this.ownerId = ownerId;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(bulletId);
        out.writeInt(ownerId);
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(velocityX);
        out.writeFloat(velocityY);
    }

    public void read(DataInputStream in) throws IOException {
        bulletId = in.readInt();
        ownerId = in.readInt();
        x = in.readFloat();
        y = in.readFloat();
        velocityX = in.readFloat();
        velocityY = in.readFloat();
    }
}