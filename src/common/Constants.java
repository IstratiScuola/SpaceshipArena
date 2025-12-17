package common;

public class Constants {
    // Network
    public static final int SERVER_PORT = 7777;
    public static final int TICK_RATE = 60;
    public static final double TICK_TIME = 1.0 / TICK_RATE;
    
    // Fisica
    public static final float MAX_SPEED = 575f;
    public static final float ACCELERATION = 360f;
    public static final float ROTATION_SPEED = 3f;
    public static final float THROTTLE_CHANGE_RATE = 1.0f;
    
    // Finestra
    public static final int WORLD_WIDTH = 1280;
    public static final int WORLD_HEIGHT = 720;
    
    // Nave
    public static final float SHIP_SIZE = 20f;
    public static final int MAX_HEALTH = 3;
    public static final float RESPAWN_TIME = 3.0f;
    public static final float INVULNERABILITY_TIME = 3.0f;
    
    // Proiettili
    public static final float BULLET_SPEED = 600f;
    public static final float BULLET_SIZE = 4f;
    public static final float BULLET_LIFETIME = 2.0f;
    public static final float SHOOT_COOLDOWN = 0.25f;
}