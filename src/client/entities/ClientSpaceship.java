package client.entities;

import common.Constants;
import common.Vector2f;
import common.network.SpaceshipState;

public class ClientSpaceship {
    private int playerId;
    private String playerName;

    private Vector2f position;
    private Vector2f targetPosition;
    private float rotation;
    private float targetRotation;
    private Vector2f velocity;
    private float throttle;
    private float[] color;

    private int health;
    private boolean alive;
    private boolean invulnerable;

    private static final float LERP_FACTOR = 0.3f;

    public ClientSpaceship(SpaceshipState state) {
        this.playerId = state.playerId;
        this.playerName = state.playerName;
        this.position = new Vector2f(state.x, state.y);
        this.targetPosition = new Vector2f(state.x, state.y);
        this.rotation = state.rotation;
        this.targetRotation = state.rotation;
        this.velocity = new Vector2f(state.velocityX, state.velocityY);
        this.throttle = state.throttle;
        this.color = state.color.clone();
        this.health = state.health;
        this.alive = state.alive;
        this.invulnerable = state.invulnerable;
    }

    public void updateFromState(SpaceshipState state) {
        this.targetPosition.set(state.x, state.y);
        this.targetRotation = state.rotation;
        this.velocity.set(state.velocityX, state.velocityY);
        this.throttle = state.throttle;
        this.health = state.health;
        this.alive = state.alive;
        this.invulnerable = state.invulnerable;
    }

    public void interpolate() {
        // Handle screen wrapping for smooth transitions
        float dx = targetPosition.x - position.x;
        float dy = targetPosition.y - position.y;

        // If the difference is too large, the ship wrapped around the screen
        // Snap to the new position instead of interpolating across the screen
        float wrapThreshold = Constants.WORLD_WIDTH / 2.0f;
        if (Math.abs(dx) > wrapThreshold) {
            position.x = targetPosition.x;
        } else {
            position.x += dx * LERP_FACTOR;
        }

        wrapThreshold = Constants.WORLD_HEIGHT / 2.0f;
        if (Math.abs(dy) > wrapThreshold) {
            position.y = targetPosition.y;
        } else {
            position.y += dy * LERP_FACTOR;
        }

        float rotDiff = targetRotation - rotation;
        while (rotDiff > Math.PI)
            rotDiff -= Math.PI * 2;
        while (rotDiff < -Math.PI)
            rotDiff += Math.PI * 2;
        rotation += rotDiff * LERP_FACTOR;
    }

    // Getters
    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public float getThrottle() {
        return throttle;
    }

    public float[] getColor() {
        return color;
    }

    public float getSpeed() {
        return velocity.length();
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return Constants.MAX_HEALTH;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }
}