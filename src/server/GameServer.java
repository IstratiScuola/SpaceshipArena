package server;

import common.Constants;
import common.network.*;

import java.io.IOException;
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

    public GameServer() {
        clients = new ConcurrentHashMap<>();
        spaceships = new ConcurrentHashMap<>();
        bullets = new ConcurrentHashMap<>();
        nextPlayerId = new AtomicInteger(1);
        nextBulletId = new AtomicInteger(1);
        currentTick = 0;
    }

    public void start() {
        running = true;

        // Start accept thread
        Thread acceptThread = new Thread(this::acceptClients);
        acceptThread.setDaemon(true);
        acceptThread.start();

        // Run game loop on main thread
        gameLoop();
    }

    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            System.out.println("Server listening on port " + Constants.SERVER_PORT);

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
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }

    private void gameLoop() {
        double lastTime = System.nanoTime() / 1_000_000_000.0;
        double accumulator = 0;

        System.out.println("Game loop started at " + Constants.TICK_RATE + " ticks/second");

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

            broadcastGameState();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void update(float dt) {
        // Process inputs and update spaceships
        for (Map.Entry<Integer, ServerSpaceship> entry : spaceships.entrySet()) {
            int playerId = entry.getKey();
            ServerSpaceship ship = entry.getValue();
            
            ClientHandler client = clients.get(playerId);
            if (client != null && client.isConnected()) {
                ship.applyInput(client.getLastInput());
            }
            
            ship.update(dt);
            
            // Check if ship wants to shoot
            if (ship.wantsToShoot() && ship.isAlive()) {
                createBullet(ship);
            }
        }
        
        // Update bullets
        Iterator<Map.Entry<Integer, ServerBullet>> bulletIterator = bullets.entrySet().iterator();
        while (bulletIterator.hasNext()) {
            Map.Entry<Integer, ServerBullet> entry = bulletIterator.next();
            ServerBullet bullet = entry.getValue();
            
            bullet.update(dt);
            
            // Remove inactive bullets
            if (!bullet.isActive()) {
                bulletIterator.remove();
                continue;
            }
            
            // Check collisions with spaceships
            checkBulletCollisions(bullet);
        }
    }

    private void createBullet(ServerSpaceship ship) {
        int bulletId = nextBulletId.getAndIncrement();
        
        // Spawn bullet at nose of ship
        float spawnX = ship.getPosition().x + 
            (float) Math.cos(ship.getRotation()) * Constants.SHIP_SIZE * 1.5f;
        float spawnY = ship.getPosition().y + 
            (float) Math.sin(ship.getRotation()) * Constants.SHIP_SIZE * 1.5f;
        
        ServerBullet bullet = new ServerBullet(
            bulletId, 
            ship.getPlayerId(), 
            spawnX, 
            spawnY, 
            ship.getRotation()
        );
        
        bullets.put(bulletId, bullet);
    }

    private void checkBulletCollisions(ServerBullet bullet) {
        for (ServerSpaceship ship : spaceships.values()) {
            // Can't hit yourself
            if (ship.getPlayerId() == bullet.getOwnerId()) {
                continue;
            }
            
            // Can't hit dead ships
            if (!ship.isAlive()) {
                continue;
            }
            
            // Can't hit invulnerable ships
            if (ship.isInvulnerable()) {
                continue;
            }
            
            // Check collision
            if (bullet.checkCollision(
                    ship.getPosition().x, 
                    ship.getPosition().y, 
                    Constants.SHIP_SIZE)) {
                
                // Hit!
                bullet.deactivate();
                boolean killed = ship.takeDamage();
                
                if (killed) {
                    // Notify all players of death
                    ServerSpaceship killer = spaceships.get(bullet.getOwnerId());
                    String killerName = killer != null ? killer.getPlayerName() : "Unknown";
                    
                    PlayerDiedPacket deathPacket = new PlayerDiedPacket(
                        ship.getPlayerId(),
                        bullet.getOwnerId(),
                        ship.getPlayerName(),
                        killerName
                    );
                    
                    broadcast(deathPacket);
                    System.out.println(ship.getPlayerName() + " was destroyed by " + killerName);
                }
                
                break;
            }
        }
    }

    private void broadcastGameState() {
        if (clients.isEmpty()) return;

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
        
        ServerSpaceship ship = new ServerSpaceship(client.getPlayerId(), client.getPlayerName());
        spaceships.put(client.getPlayerId(), ship);

        PlayerJoinedPacket joinPacket = new PlayerJoinedPacket(
            client.getPlayerId(), 
            client.getPlayerName()
        );
        
        for (ClientHandler other : clients.values()) {
            if (other.getPlayerId() != client.getPlayerId() && other.isConnected()) {
                other.sendPacket(joinPacket);
            }
        }
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