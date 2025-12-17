package client.core;

import client.entities.ClientBullet;
import client.entities.ClientSpaceship;
import client.graphics.Renderer;
import client.network.NetworkClient;
import common.Constants;
import common.GamePhase;
import common.network.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.*;
//classe che inizializza tutto il client 
public class Game {
    private Window window;
    private Input input;
    private Renderer renderer;
    private NetworkClient network;

    private String serverAddress;
    private String playerName;

    private Map<Integer, ClientSpaceship> spaceships;
    private Map<Integer, ClientBullet> bullets;
    private List<String> killFeed;
    private static final int MAX_KILL_FEED = 5;
    private static final float KILL_FEED_DURATION = 5.0f;
    private List<Float> killFeedTimers;

    private boolean running;
    private boolean readyKeyWasPressed;

    public Game(String serverAddress, String playerName) {
        this.serverAddress = serverAddress;
        this.playerName = playerName;
        this.spaceships = new ConcurrentHashMap<>();
        this.bullets = new ConcurrentHashMap<>();
        this.killFeed = new ArrayList<>();
        this.killFeedTimers = new ArrayList<>();
        this.readyKeyWasPressed = false;
    }

    public void run() {
        init();
        if (running) {
            loop();
        }
        cleanup();
    }

    private void init() {
        window = new Window(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT,
                "Spaceship Arena");
        window.create();

        input = new Input(window);
        renderer = new Renderer();

        network = new NetworkClient(serverAddress, playerName);
        if (!network.connect()) {
            System.err.println("Connessione al server fallita");
            running = false;
            return;
        }

        Time.init();
        Time.setTargetFPS(60);

        running = true;
    }

    private void loop() {
        while (running && !window.shouldClose() && network.isConnected()) {
            Time.update();
            float dt = Time.getDeltaTime();

            GamePhase phase = network.getCurrentPhase();

            if (phase == GamePhase.LOBBY || phase == GamePhase.GAME_OVER) {
                handleLobbyInput();
                updateLobby();
                renderLobby();
            } else {
                handleInput();
                processPackets();
                update(dt);
                render();
            }

            window.update();
            Time.sync();
        }
    }

    private void handleLobbyInput() {
        if (input.isKeyDown(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getHandle(), true);
        }

        // pronto
        boolean readyKeyPressed = input.isKeyDown(GLFW_KEY_SPACE) || input.isKeyDown(GLFW_KEY_ENTER);
        if (readyKeyPressed && !readyKeyWasPressed) {
            if (network.getCurrentPhase() == GamePhase.LOBBY) {
                network.sendReadyToggle();
            }
        }
        readyKeyWasPressed = readyKeyPressed;
    }

    private void updateLobby() {
        Packet packet;
        while ((packet = network.pollPacket()) != null) {
            if (packet.getType() == PacketType.GAME_START) {
                spaceships.clear();
                bullets.clear();
                killFeed.clear();
                killFeedTimers.clear();
            }
        }
    }

    private void renderLobby() {
        renderer.clear();

        LobbyStatePacket lobbyState = network.getLastLobbyState();
        GameOverPacket gameOver = network.getLastGameOver();

        renderer.renderLobby(lobbyState, network.getPlayerId(), gameOver);
    }

    private void handleInput() {
        if (input.isKeyDown(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getHandle(), true);
        }

        boolean rotateLeft = input.isKeyDown(GLFW_KEY_A);
        boolean rotateRight = input.isKeyDown(GLFW_KEY_D);
        boolean throttleUp = input.isKeyDown(GLFW_KEY_W);
        boolean throttleDown = input.isKeyDown(GLFW_KEY_S);
        boolean throttleMax = input.isKeyDown(GLFW_KEY_LEFT_SHIFT) ||
                input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
        boolean throttleZero = input.isKeyDown(GLFW_KEY_LEFT_CONTROL) ||
                input.isKeyDown(GLFW_KEY_RIGHT_CONTROL);
        boolean shoot = input.isKeyDown(GLFW_KEY_SPACE);

        network.sendInput(rotateLeft, rotateRight, throttleUp, throttleDown,
                throttleMax, throttleZero, shoot);
    }

    private void processPackets() {
        Packet packet;
        while ((packet = network.pollPacket()) != null) {
            if (packet.getType() == PacketType.PLAYER_DIED) {
                PlayerDiedPacket died = (PlayerDiedPacket) packet;
                addKillFeedEntry(died.killerName + " destroyed " + died.playerName);
            }
        }
    }

    private void addKillFeedEntry(String message) {
        killFeed.add(0, message);
        killFeedTimers.add(0, KILL_FEED_DURATION);

        while (killFeed.size() > MAX_KILL_FEED) {
            killFeed.remove(killFeed.size() - 1);
            killFeedTimers.remove(killFeedTimers.size() - 1);
        }
    }

    private void update(float dt) {
        for (int i = killFeedTimers.size() - 1; i >= 0; i--) {
            killFeedTimers.set(i, killFeedTimers.get(i) - dt);
            if (killFeedTimers.get(i) <= 0) {
                killFeed.remove(i);
                killFeedTimers.remove(i);
            }
        }

        GameStatePacket gameState = network.getLastGameState();
        if (gameState != null) {
            for (SpaceshipState state : gameState.spaceships) {
                ClientSpaceship ship = spaceships.get(state.playerId);
                if (ship == null) {
                    ship = new ClientSpaceship(state);
                    spaceships.put(state.playerId, ship);
                } else {
                    ship.updateFromState(state);
                }
            }

            spaceships.keySet().removeIf(id -> gameState.spaceships.stream().noneMatch(s -> s.playerId == id));

            bullets.clear();
            for (BulletState state : gameState.bullets) {
                ClientBullet bullet = new ClientBullet(state);
                bullets.put(state.bulletId, bullet);
            }
        }

        for (ClientSpaceship ship : spaceships.values()) {
            ship.interpolate();
        }
    }

    private void render() {
        renderer.clear();


        for (ClientBullet bullet : bullets.values()) {
            ClientSpaceship owner = spaceships.get(bullet.getOwnerId());
            float[] color = owner != null ? owner.getColor() : new float[] { 1, 1, 1 };
            renderer.renderBullet(bullet, color);
        }

        for (ClientSpaceship ship : spaceships.values()) {
            boolean isLocalPlayer = (ship.getPlayerId() == network.getPlayerId());
            renderer.renderSpaceship(ship, isLocalPlayer);
        }

        ClientSpaceship localShip = spaceships.get(network.getPlayerId());
        if (localShip != null) {
            renderer.renderHUD(localShip, Time.getFPS(), spaceships.size());

            if (!localShip.isAlive()) {
                renderer.renderDeathScreen();
            }
        }

        renderer.renderKillFeed(killFeed, killFeedTimers);
    }

    private void cleanup() {
        if (network != null) {
            network.disconnect();
        }
        if (window != null) {
            window.destroy();
        }
    }
}