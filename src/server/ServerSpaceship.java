package server;

import common.Constants;
import common.Vector2f;
import common.network.InputPacket;
import common.network.SpaceshipState;

import java.util.Random;

public class ServerSpaceship {
    private int playerId;
    private String playerName;
    
    // Transform
    private Vector2f position;
    private float rotation;
    
    // Physics
    private Vector2f velocity;
    private float throttle;
    
    // Visual
    private float[] color;
    
    // Combat
    private int health;
    private boolean alive;
    private float respawnTimer;
    private float invulnerabilityTimer;
    private float shootCooldown;
    private boolean wantsToShoot;
    
    // Input state
    private boolean rotateLeft;
    private boolean rotateRight;
    private boolean throttleUp;
    private boolean throttleDown;
    private boolean throttleMax;
    private boolean throttleZero;
    private boolean shoot;
    
    private Random random;

    public ServerSpaceship(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.random = new Random();
        
        // Random color for each player
        this.color = new float[] {
            0.3f + random.nextFloat() * 0.7f,
            0.3f + random.nextFloat() * 0.7f,
            0.3f + random.nextFloat() * 0.7f
        };
        
        spawn();
    }

    public void spawn() {
        // Random spawn position
        this.position = new Vector2f(
            100 + random.nextFloat() * (Constants.WORLD_WIDTH - 200),
            100 + random.nextFloat() * (Constants.WORLD_HEIGHT - 200)
        );
        
        this.rotation = random.nextFloat() * (float) Math.PI * 2;
        this.velocity = new Vector2f();
        this.throttle = 0f;
        
        this.health = Constants.MAX_HEALTH;
        this.alive = true;
        this.respawnTimer = 0;
        this.invulnerabilityTimer = Constants.INVULNERABILITY_TIME;
        this.shootCooldown = 0;
        this.wantsToShoot = false;
    }

    public void applyInput(InputPacket input) {
        this.rotateLeft = input.rotateLeft;
        this.rotateRight = input.rotateRight;
        this.throttleUp = input.throttleUp;
        this.throttleDown = input.throttleDown;
        this.throttleMax = input.throttleMax;
        this.throttleZero = input.throttleZero;
        this.shoot = input.shoot;
    }

    public void update(float dt) {
        // Handle respawn timer
        if (!alive) {
            respawnTimer -= dt;
            if (respawnTimer <= 0) {
                spawn();
            }
            return;
        }
        
        // Decrease invulnerability
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= dt;
        }
        
        // Decrease shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown -= dt;
        }
        
        // Check if wants to shoot
        wantsToShoot = shoot && shootCooldown <= 0;
        if (wantsToShoot) {
            shootCooldown = Constants.SHOOT_COOLDOWN;
        }

        // Handle rotation
        if (rotateLeft) {
            rotation -= Constants.ROTATION_SPEED * dt;
        }
        if (rotateRight) {
            rotation += Constants.ROTATION_SPEED * dt;
        }

        // Handle throttle
        if (throttleMax) {
            throttle = 1.0f;
        } else if (throttleZero) {
            throttle = 0.0f;
        } else {
            if (throttleUp) {
                throttle += Constants.THROTTLE_CHANGE_RATE * dt;
                if (throttle > 1.0f) throttle = 1.0f;
            }
            if (throttleDown) {
                throttle -= Constants.THROTTLE_CHANGE_RATE * dt;
                if (throttle < 0.0f) throttle = 0.0f;
            }
        }

        // Apply thrust
        if (throttle > 0) {
            float thrustX = (float) Math.cos(rotation) * Constants.ACCELERATION * throttle * dt;
            float thrustY = (float) Math.sin(rotation) * Constants.ACCELERATION * throttle * dt;
            velocity.x += thrustX;
            velocity.y += thrustY;
        }

        // Clamp velocity
        velocity.clampLength(Constants.MAX_SPEED);

        // Update position
        position.addLocal(velocity.scale(dt));

        // Screen wrapping
        float size = Constants.SHIP_SIZE;
        if (position.x < -size) position.x = Constants.WORLD_WIDTH + size;
        if (position.x > Constants.WORLD_WIDTH + size) position.x = -size;
        if (position.y < -size) position.y = Constants.WORLD_HEIGHT + size;
        if (position.y > Constants.WORLD_HEIGHT + size) position.y = -size;

        // Normalize rotation
        while (rotation > Math.PI * 2) rotation -= Math.PI * 2;
        while (rotation < 0) rotation += Math.PI * 2;
    }

    public boolean takeDamage() {
        if (!alive || invulnerabilityTimer > 0) {
            return false;
        }
        
        health--;
        if (health <= 0) {
            die();
            return true;
        }
        
        // Brief invulnerability after hit
        invulnerabilityTimer = 0.5f;
        return false;
    }

    public void die() {
        alive = false;
        respawnTimer = Constants.RESPAWN_TIME;
        velocity.set(0, 0);
        throttle = 0;
    }

    public boolean wantsToShoot() {
        boolean result = wantsToShoot;
        wantsToShoot = false;
        return result;
    }

    public SpaceshipState getState() {
        return new SpaceshipState(
            playerId,
            playerName,
            position.x,
            position.y,
            rotation,
            velocity.x,
            velocity.y,
            throttle,
            color,
            health,
            alive,
            invulnerabilityTimer > 0
        );
    }

    // Getters
    public int getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public Vector2f getPosition() { return position; }
    public float getRotation() { return rotation; }
    public boolean isAlive() { return alive; }
    public boolean isInvulnerable() { return invulnerabilityTimer > 0; }
    public int getHealth() { return health; }
}