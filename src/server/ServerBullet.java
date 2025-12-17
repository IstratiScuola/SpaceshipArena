package server;

import common.Constants;
import common.Vector2f;
import common.network.BulletState;

public class ServerBullet {
    private int bulletId;
    private int ownerId;
    private Vector2f position;
    private Vector2f velocity;
    private float lifetime;
    private boolean active;

    public ServerBullet(int bulletId, int ownerId, float x, float y, float rotation) {
        this.bulletId = bulletId;
        this.ownerId = ownerId;
        this.position = new Vector2f(x, y);

        // Calculate velocity based on ship rotation
        float vx = (float) Math.cos(rotation) * Constants.BULLET_SPEED;
        float vy = (float) Math.sin(rotation) * Constants.BULLET_SPEED;
        this.velocity = new Vector2f(vx, vy);

        this.lifetime = Constants.BULLET_LIFETIME;
        this.active = true;
    }

    public void update(float dt) {
        if (!active)
            return;

        // Move bullet
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // Deactivate bullet if it goes off-screen
        if (position.x < 0 || position.x > Constants.WORLD_WIDTH ||
                position.y < 0 || position.y > Constants.WORLD_HEIGHT) {
            active = false;
            return;
        }

        // Decrease lifetime
        lifetime -= dt;
        if (lifetime <= 0) {
            active = false;
        }
    }

    public boolean checkCollision(float targetX, float targetY, float targetRadius) {
        if (!active)
            return false;

        float dx = position.x - targetX;
        float dy = position.y - targetY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        return distance < (Constants.BULLET_SIZE + targetRadius);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public int getBulletId() {
        return bulletId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public BulletState getState() {
        return new BulletState(
                bulletId,
                ownerId,
                position.x,
                position.y,
                velocity.x,
                velocity.y);
    }
}