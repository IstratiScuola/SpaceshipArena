package client.entities;

import common.Vector2f;
import common.network.BulletState;

public class ClientBullet {
    private int bulletId;
    private int ownerId;
    private Vector2f position;
    private Vector2f velocity;

    public ClientBullet(BulletState state) {
        this.bulletId = state.bulletId;
        this.ownerId = state.ownerId;
        this.position = new Vector2f(state.x, state.y);
        this.velocity = new Vector2f(state.velocityX, state.velocityY);
    }

    public void updateFromState(BulletState state) {
        this.position.set(state.x, state.y);
        this.velocity.set(state.velocityX, state.velocityY);
    }

    public int getBulletId() { return bulletId; }
    public int getOwnerId() { return ownerId; }
    public Vector2f getPosition() { return position; }
    public Vector2f getVelocity() { return velocity; }
}