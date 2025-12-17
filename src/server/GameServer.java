package server;

import common.Constants;
import common.GamePhase;
import common.network.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
    private ServerSocket serverSocket;
    private boolean running;

    private Map<Integer, ClientHandler> clients;
    private Map<Integer, ServerSpaceship> spaceships;
    private Map<Integer, ServerBullet> bullets;

    private AtomicInteger nextPlayerId;
    private AtomicInteger nextBulletId;

    private long currentTick;

    private GamePhase gamePhase;
    private float gameOverTimer;
    private static final float GAME_OVER_DURATION = 5.0f;
    private String winnerName;
    private int winnerId;

    public GameServer() {
        clients = new ConcurrentHashMap<>();
        spaceships = new ConcurrentHashMap<>();
        bullets = new ConcurrentHashMap<>();
        nextPlayerId = new AtomicInteger(1);
        nextBulletId = new AtomicInteger(1);
        currentTick = 0;
        gamePhase = GamePhase.LOBBY;
        gameOverTimer = 0;
        winnerName = "";
        winnerId = -1;
    }

    public void start() {
        running = true;
        Thread acceptThread = new Thread(this::acceptClients);
        acceptThread.setDaemon(true);
        acceptThread.start();

        // LOOP DEL GIOCO
        gameLoop();
    }

    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT, 50, InetAddress.getByName("0.0.0.0"));
            System.out.println("Server su port " + Constants.SERVER_PORT);
            System.out.println("IP LAN: " + InetAddress.getLocalHost().getHostAddress());

            while (running) {
                Socket clientSocket = serverSocket.accept();
                int playerId = nextPlayerId.getAndIncrement();

                ClientHandler handler = new ClientHandler(clientSocket, this, playerId);
                Thread clientThread = new Thread(handler);
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("errore server: " + e.getMessage());
            }
        }
    }

    private void gameLoop() {
        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double accumulator = 0;


        while (running) {
            double currentTime = System.nanoTime() / 1_000_000_000.0;
            double frameTime = currentTime - lastTime;
            lastTime = currentTime;

            accumulator += frameTime;

            while (accumulator >= Constants.TICK_TIME) {
                update((float) Constants.TICK_TIME);
                accumulator -= Constants.TICK_TIME;
                currentTick++;
            }

            if (gamePhase == GamePhase.LOBBY) {
                broadcastLobbyState();
            } else {
                broadcastGameState();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void update(float dt) {
        if (gamePhase == GamePhase.LOBBY) {

            checkGameStart();
            return;
        }

        if (gamePhase == GamePhase.GAME_OVER) {
            gameOverTimer -= dt;
            if (gameOverTimer <= 0) {
                returnToLobby();
            }
            return;
        }

        for (Map.Entry<Integer, ServerSpaceship> entry : spaceships.entrySet()) {
            int playerId = entry.getKey();
            ServerSpaceship ship = entry.getValue();

            ClientHandler client = clients.get(playerId);
            if (client != null && client.isConnected()) {
                ship.applyInput(client.getLastInput());
            }

            ship.update(dt);

            if (ship.wantsToShoot() && ship.isAlive()) {
                createBullet(ship);
            }
        }
        Iterator<Map.Entry<Integer, ServerBullet>> bulletIterator = bullets.entrySet().iterator();
        while (bulletIterator.hasNext()) {
            Map.Entry<Integer, ServerBullet> entry = bulletIterator.next();
            ServerBullet bullet = entry.getValue();

            bullet.update(dt);

            if (!bullet.isActive()) {
                bulletIterator.remove();
                continue;
            }

            checkBulletCollisions(bullet);
        }


        checkForWinner();
    }

    private void checkGameStart() {
        if (clients.size() < 2) {
            return;
        }

        for (ClientHandler client : clients.values()) {
            if (!client.isReady()) {
                return;
            }
        }

        startGame();
    }

    private void startGame() {

        gamePhase = GamePhase.PLAYING;

        spaceships.clear();
        bullets.clear();
        for (ClientHandler client : clients.values()) {
            ServerSpaceship ship = new ServerSpaceship(client.getPlayerId(), client.getPlayerName());
            ship.setRespawnEnabled(false); 
            spaceships.put(client.getPlayerId(), ship);
        }


        broadcast(new GameStartPacket());
    }

    private void checkForWinner() {
        if (gamePhase != GamePhase.PLAYING) {
            return;
        }

        List<ServerSpaceship> alivePlayers = new ArrayList<>();
        for (ServerSpaceship ship : spaceships.values()) {
            if (ship.isAlive()) {
                alivePlayers.add(ship);
            }
        }

        if (alivePlayers.size() <= 1 && spaceships.size() > 1) {
            gamePhase = GamePhase.GAME_OVER;
            gameOverTimer = GAME_OVER_DURATION;

            if (alivePlayers.size() == 1) {
                ServerSpaceship winner = alivePlayers.get(0);
                winnerId = winner.getPlayerId();
                winnerName = winner.getPlayerName();

            } else {
                winnerId = -1;
                winnerName = "Nessuno";

            }

            broadcast(new GameOverPacket(winnerId, winnerName));
        }
    }

    private void returnToLobby() {
       // System.out.println("ritonro lobby");
        gamePhase = GamePhase.LOBBY;

        for (ClientHandler client : clients.values()) {
            client.setReady(false);
        }

        spaceships.clear();
        bullets.clear();

        winnerId = -1;
        winnerName = "";
    }

    private void createBullet(ServerSpaceship ship) {
        int bulletId = nextBulletId.getAndIncrement();

        float spawnX = ship.getPosition().x +
                (float) Math.cos(ship.getRotation()) * Constants.SHIP_SIZE * 1.5f;
        float spawnY = ship.getPosition().y +
                (float) Math.sin(ship.getRotation()) * Constants.SHIP_SIZE * 1.5f;

        ServerBullet bullet = new ServerBullet(
                bulletId,
                ship.getPlayerId(),
                spawnX,
                spawnY,
                ship.getRotation());

        bullets.put(bulletId, bullet);
    }

    private void checkBulletCollisions(ServerBullet bullet) {
        for (ServerSpaceship ship : spaceships.values()) {
            if (ship.getPlayerId() == bullet.getOwnerId()) {
                continue;
            }
            if (!ship.isAlive()) {
                continue;
            }

            if (ship.isInvulnerable()) {
                continue;
            }

            if (bullet.checkCollision(
                    ship.getPosition().x,
                    ship.getPosition().y,
                    Constants.SHIP_SIZE)) {

                bullet.deactivate();
                boolean killed = ship.takeDamage();

                if (killed) {

                    ServerSpaceship killer = spaceships.get(bullet.getOwnerId());
                    String killerName = killer != null ? killer.getPlayerName() : "Unknown";

                    PlayerDiedPacket deathPacket = new PlayerDiedPacket(
                            ship.getPlayerId(),
                            bullet.getOwnerId(),
                            ship.getPlayerName(),
                            killerName);

                    broadcast(deathPacket);
                    System.out.println(ship.getPlayerName() + " was destroyed by " + killerName);
                }

                break;
            }
        }
    }

    private void broadcastLobbyState() {
        if (clients.isEmpty())
            return;

        List<LobbyStatePacket.LobbyPlayer> players = new ArrayList<>();
        for (ClientHandler client : clients.values()) {
            players.add(new LobbyStatePacket.LobbyPlayer(
                    client.getPlayerId(),
                    client.getPlayerName(),
                    client.isReady()));
        }

        LobbyStatePacket packet = new LobbyStatePacket(gamePhase, players);
        broadcast(packet);
    }

    private void broadcastGameState() {
        if (clients.isEmpty())
            return;

        // Collect all spaceship states
        List<SpaceshipState> shipStates = new ArrayList<>();
        for (ServerSpaceship ship : spaceships.values()) {
            shipStates.add(ship.getState());
        }

        // Collect all bullet states
        List<BulletState> bulletStates = new ArrayList<>();
        for (ServerBullet bullet : bullets.values()) {
            if (bullet.isActive()) {
                bulletStates.add(bullet.getState());
            }
        }

        GameStatePacket packet = new GameStatePacket(shipStates, bulletStates, currentTick);

        for (ClientHandler client : clients.values()) {
            if (client.isConnected()) {
                client.sendPacket(packet);
            }
        }
    }

    private void broadcast(Packet packet) {
        for (ClientHandler client : clients.values()) {
            if (client.isConnected()) {
                client.sendPacket(packet);
            }
        }
    }

    public synchronized void onPlayerJoined(ClientHandler client) {
        clients.put(client.getPlayerId(), client);

        PlayerJoinedPacket joinPacket = new PlayerJoinedPacket(
                client.getPlayerId(),
                client.getPlayerName());

        for (ClientHandler other : clients.values()) {
            if (other.getPlayerId() != client.getPlayerId() && other.isConnected()) {
                other.sendPacket(joinPacket);
            }
        }

        System.out.println("Players in lobby: " + clients.size());
    }

    public synchronized void onPlayerReadyChanged(ClientHandler client) {
        System.out.println(client.getPlayerName() + " is " + (client.isReady() ? "ready" : "not ready"));
    }

    public synchronized void onPlayerLeft(ClientHandler client) {
        clients.remove(client.getPlayerId());
        spaceships.remove(client.getPlayerId());

        PlayerLeftPacket leftPacket = new PlayerLeftPacket(client.getPlayerId());
        for (ClientHandler other : clients.values()) {
            if (other.isConnected()) {
                other.sendPacket(leftPacket);
            }
        }

        // If we're playing and someone leaves, check for winner
        if (gamePhase == GamePhase.PLAYING) {
            // Remove their spaceship
            spaceships.remove(client.getPlayerId());
            checkForWinner();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
}