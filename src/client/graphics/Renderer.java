package client.graphics;

import client.entities.ClientBullet;
import client.entities.ClientSpaceship;
import common.Constants;
import common.Vector2f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    public void clear() {
        glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void renderBullet(ClientBullet bullet, float[] color) {
        Vector2f pos = bullet.getPosition();
        float size = Constants.BULLET_SIZE;

        glPushMatrix();
        glTranslatef(pos.x, pos.y, 0);

        // Glow effect
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(color[0], color[1], color[2], 0.3f);
        glVertex2f(0, 0);
        for (int i = 0; i <= 16; i++) {
            float angle = (float) (i * Math.PI * 2 / 16);
            glVertex2f((float) Math.cos(angle) * size * 2,
                       (float) Math.sin(angle) * size * 2);
        }
        glEnd();

        // Core
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(0, 0);
        glColor4f(color[0], color[1], color[2], 1.0f);
        for (int i = 0; i <= 16; i++) {
            float angle = (float) (i * Math.PI * 2 / 16);
            glVertex2f((float) Math.cos(angle) * size,
                       (float) Math.sin(angle) * size);
        }
        glEnd();

        glPopMatrix();
    }

    public void renderSpaceship(ClientSpaceship ship, boolean isLocalPlayer) {
        if (!ship.isAlive()) {
            renderExplosion(ship);
            return;
        }

        Vector2f pos = ship.getPosition();
        float rot = ship.getRotation();
        float size = Constants.SHIP_SIZE;
        float[] color = ship.getColor();
        float throttle = ship.getThrottle();
        boolean invulnerable = ship.isInvulnerable();

        glPushMatrix();
        glTranslatef(pos.x, pos.y, 0);
        glRotatef((float) Math.toDegrees(rot), 0, 0, 1);

        // Invulnerability shield effect
        if (invulnerable) {
            float pulse = (float) (0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.01));
            glBegin(GL_LINE_LOOP);
            glColor4f(0.5f, 0.5f, 1.0f, pulse);
            for (int i = 0; i < 16; i++) {
                float angle = (float) (i * Math.PI * 2 / 16);
                glVertex2f((float) Math.cos(angle) * size * 1.8f,
                           (float) Math.sin(angle) * size * 1.8f);
            }
            glEnd();
        }

        // Engine flame
        if (throttle > 0.05f) {
            drawFlame(size, throttle);
        }

        // Ship body - flash when invulnerable
        float alpha = invulnerable ? 
            (float) (0.5f + 0.5f * Math.sin(System.currentTimeMillis() * 0.02)) : 1.0f;
        
        glBegin(GL_TRIANGLES);
        glColor4f(color[0], color[1], color[2], alpha);
        glVertex2f(size * 1.5f, 0);
        glVertex2f(-size, -size * 0.8f);
        glVertex2f(-size, size * 0.8f);
        glEnd();

        // Outline
        glLineWidth(isLocalPlayer ? 3.0f : 2.0f);
        glBegin(GL_LINE_LOOP);
        if (isLocalPlayer) {
            glColor4f(1.0f, 1.0f, 1.0f, alpha);
        } else {
            glColor4f(0.7f, 0.7f, 0.7f, alpha);
        }
        glVertex2f(size * 1.5f, 0);
        glVertex2f(-size, -size * 0.8f);
        glVertex2f(-size * 0.5f, 0);
        glVertex2f(-size, size * 0.8f);
        glEnd();

        // Cockpit
        glBegin(GL_TRIANGLES);
        glColor4f(0.1f, 0.1f, 0.3f, alpha);
        glVertex2f(size * 0.8f, 0);
        glVertex2f(-size * 0.2f, -size * 0.3f);
        glVertex2f(-size * 0.2f, size * 0.3f);
        glEnd();

        glPopMatrix();

        // Player name
        drawPlayerName(pos.x, pos.y - size - 15, ship.getPlayerName(), isLocalPlayer);
        
        // Health bar above name
        drawHealthBar(pos.x, pos.y - size - 30, ship.getHealth(), ship.getMaxHealth());
    }

    private void renderExplosion(ClientSpaceship ship) {
        Vector2f pos = ship.getPosition();
        float[] color = ship.getColor();
        
        // Simple explosion animation
        float time = (System.currentTimeMillis() % 1000) / 1000.0f;
        float size = Constants.SHIP_SIZE * (1 + time * 2);
        float alpha = 1.0f - time;
        
        glPushMatrix();
        glTranslatef(pos.x, pos.y, 0);

        // Explosion particles
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * Math.PI * 2 / 8);
            float dist = size * time * 2;
            float px = (float) Math.cos(angle) * dist;
            float py = (float) Math.sin(angle) * dist;
            
            glBegin(GL_TRIANGLES);
            glColor4f(color[0], color[1], color[2], alpha);
            glVertex2f(px, py - 5);
            glVertex2f(px - 5, py + 5);
            glVertex2f(px + 5, py + 5);
            glEnd();
        }

        // Central flash
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(1.0f, 0.8f, 0.3f, alpha * 0.5f);
        glVertex2f(0, 0);
        for (int i = 0; i <= 16; i++) {
            float angle = (float) (i * Math.PI * 2 / 16);
            glVertex2f((float) Math.cos(angle) * size,
                       (float) Math.sin(angle) * size);
        }
        glEnd();

        glPopMatrix();
    }

    private void drawFlame(float shipSize, float throttle) {
        float flameLength = shipSize * (0.5f + throttle * 1.5f);
        float flameWidth = shipSize * 0.4f * throttle;

        glBegin(GL_TRIANGLES);
        glColor4f(1.0f, 0.5f, 0.0f, 0.8f);
        glVertex2f(-shipSize * 0.6f, flameWidth);
        glVertex2f(-shipSize * 0.6f, -flameWidth);
        glColor4f(1.0f, 1.0f, 0.0f, 0.0f);
        glVertex2f(-shipSize - flameLength, 0);
        glEnd();

        glBegin(GL_TRIANGLES);
        glColor4f(0.8f, 0.9f, 1.0f, 0.9f);
        glVertex2f(-shipSize * 0.6f, flameWidth * 0.5f);
        glVertex2f(-shipSize * 0.6f, -flameWidth * 0.5f);
        glColor4f(0.5f, 0.7f, 1.0f, 0.0f);
        glVertex2f(-shipSize - flameLength * 0.6f, 0);
        glEnd();
    }

    private void drawHealthBar(float x, float y, int health, int maxHealth) {
        float barWidth = 40;
        float barHeight = 6;
        float segmentWidth = barWidth / maxHealth;

        // Background
        glBegin(GL_QUADS);
        glColor4f(0.2f, 0.0f, 0.0f, 0.8f);
        glVertex2f(x - barWidth/2, y);
        glVertex2f(x + barWidth/2, y);
        glVertex2f(x + barWidth/2, y + barHeight);
        glVertex2f(x - barWidth/2, y + barHeight);
        glEnd();

        // Health segments
        for (int i = 0; i < health; i++) {
            float segX = x - barWidth/2 + i * segmentWidth + 1;
            glBegin(GL_QUADS);
            
            // Color based on health level
            if (health == 1) {
                glColor4f(1.0f, 0.2f, 0.2f, 1.0f); // Red - critical
            } else if (health == 2) {
                glColor4f(1.0f, 0.8f, 0.2f, 1.0f); // Yellow - warning
            } else {
                glColor4f(0.2f, 1.0f, 0.2f, 1.0f); // Green - healthy
            }
            
            glVertex2f(segX, y + 1);
            glVertex2f(segX + segmentWidth - 2, y + 1);
            glVertex2f(segX + segmentWidth - 2, y + barHeight - 1);
            glVertex2f(segX, y + barHeight - 1);
            glEnd();
        }

        // Border
        glLineWidth(1.0f);
        glBegin(GL_LINE_LOOP);
        glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
        glVertex2f(x - barWidth/2, y);
        glVertex2f(x + barWidth/2, y);
        glVertex2f(x + barWidth/2, y + barHeight);
        glVertex2f(x - barWidth/2, y + barHeight);
        glEnd();
    }

    private void drawPlayerName(float x, float y, String name, boolean isLocal) {
        float width = name.length() * 6;
        glBegin(GL_QUADS);
        if (isLocal) {
            glColor4f(0.2f, 0.8f, 0.2f, 0.7f);
        } else {
            glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
        }
        glVertex2f(x - width/2, y);
        glVertex2f(x + width/2, y);
        glVertex2f(x + width/2, y + 12);
        glVertex2f(x - width/2, y + 12);
        glEnd();
    }

    public void renderHUD(ClientSpaceship ship, int fps, int playerCount) {
        float throttle = ship.getThrottle();
        float speed = ship.getSpeed();

        // Throttle bar
        float barX = 20, barY = 20;
        float barWidth = 20, barHeight = 150;

        glBegin(GL_QUADS);
        glColor4f(0.2f, 0.2f, 0.2f, 0.8f);
        glVertex2f(barX, barY);
        glVertex2f(barX + barWidth, barY);
        glVertex2f(barX + barWidth, barY + barHeight);
        glVertex2f(barX, barY + barHeight);
        glEnd();

        float throttleHeight = barHeight * throttle;
        glBegin(GL_QUADS);
        glColor4f(throttle, 1.0f - throttle * 0.5f, 0.2f, 1.0f);
        glVertex2f(barX + 2, barY + barHeight - throttleHeight);
        glVertex2f(barX + barWidth - 2, barY + barHeight - throttleHeight);
        glVertex2f(barX + barWidth - 2, barY + barHeight - 2);
        glVertex2f(barX + 2, barY + barHeight - 2);
        glEnd();

        glLineWidth(2.0f);
        glBegin(GL_LINE_LOOP);
        glColor3f(0.8f, 0.8f, 0.8f);
        glVertex2f(barX, barY);
        glVertex2f(barX + barWidth, barY);
        glVertex2f(barX + barWidth, barY + barHeight);
        glVertex2f(barX, barY + barHeight);
        glEnd();

        // Speed bar
        float speedBarX = 50;
        glBegin(GL_QUADS);
        glColor4f(0.2f, 0.2f, 0.2f, 0.8f);
        glVertex2f(speedBarX, barY);
        glVertex2f(speedBarX + barWidth, barY);
        glVertex2f(speedBarX + barWidth, barY + barHeight);
        glVertex2f(speedBarX, barY + barHeight);
        glEnd();

        float speedRatio = Math.min(speed / Constants.MAX_SPEED, 1.0f);
        float speedHeight = barHeight * speedRatio;
        glBegin(GL_QUADS);
        glColor4f(0.2f, 0.6f, 1.0f, 1.0f);
        glVertex2f(speedBarX + 2, barY + barHeight - speedHeight);
        glVertex2f(speedBarX + barWidth - 2, barY + barHeight - speedHeight);
        glVertex2f(speedBarX + barWidth - 2, barY + barHeight - 2);
        glVertex2f(speedBarX + 2, barY + barHeight - 2);
        glEnd();

        glBegin(GL_LINE_LOOP);
        glColor3f(0.8f, 0.8f, 0.8f);
        glVertex2f(speedBarX, barY);
        glVertex2f(speedBarX + barWidth, barY);
        glVertex2f(speedBarX + barWidth, barY + barHeight);
        glVertex2f(speedBarX, barY + barHeight);
        glEnd();

        // Health display in HUD
        renderHealthHUD(20, barY + barHeight + 20, ship.getHealth(), ship.getMaxHealth());

        // Compass
        drawCompass(Constants.WORLD_WIDTH - 80, 80, 50, ship.getRotation());

        // Player count
        drawPlayerCount(Constants.WORLD_WIDTH - 100, Constants.WORLD_HEIGHT - 30, playerCount);

        // FPS
        drawFPSIndicator(Constants.WORLD_WIDTH - 60, 20, fps);
    }

    private void renderHealthHUD(float x, float y, int health, int maxHealth) {
        float heartSize = 15;
        float spacing = 20;

        for (int i = 0; i < maxHealth; i++) {
            float hx = x + i * spacing;
            
            if (i < health) {
                // Full heart
                glBegin(GL_TRIANGLES);
                glColor4f(1.0f, 0.2f, 0.2f, 1.0f);
                // Simple heart shape (two triangles)
                glVertex2f(hx + heartSize/2, y + heartSize);
                glVertex2f(hx, y + heartSize/3);
                glVertex2f(hx + heartSize, y + heartSize/3);
                glEnd();
                
                glBegin(GL_TRIANGLE_FAN);
                glColor4f(1.0f, 0.2f, 0.2f, 1.0f);
                glVertex2f(hx + heartSize/4, y + heartSize/3);
                for (int j = 0; j <= 8; j++) {
                    float angle = (float) (Math.PI + j * Math.PI / 8);
                    glVertex2f(hx + heartSize/4 + (float)Math.cos(angle) * heartSize/4,
                               y + heartSize/3 + (float)Math.sin(angle) * heartSize/4);
                }
                glEnd();
                
                glBegin(GL_TRIANGLE_FAN);
                glColor4f(1.0f, 0.2f, 0.2f, 1.0f);
                glVertex2f(hx + 3*heartSize/4, y + heartSize/3);
                for (int j = 0; j <= 8; j++) {
                    float angle = (float) (Math.PI + j * Math.PI / 8);
                    glVertex2f(hx + 3*heartSize/4 + (float)Math.cos(angle) * heartSize/4,
                               y + heartSize/3 + (float)Math.sin(angle) * heartSize/4);
                }
                glEnd();
            } else {
                // Empty heart outline
                glLineWidth(2.0f);
                glBegin(GL_LINE_LOOP);
                glColor4f(0.5f, 0.1f, 0.1f, 1.0f);
                glVertex2f(hx + heartSize/2, y + heartSize);
                glVertex2f(hx, y + heartSize/3);
                glVertex2f(hx + heartSize/4, y);
                glVertex2f(hx + heartSize/2, y + heartSize/4);
                glVertex2f(hx + 3*heartSize/4, y);
                glVertex2f(hx + heartSize, y + heartSize/3);
                glEnd();
            }
        }
    }

    public void renderDeathScreen() {
        // Dark overlay
        glBegin(GL_QUADS);
        glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        glVertex2f(0, 0);
        glVertex2f(Constants.WORLD_WIDTH, 0);
        glVertex2f(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        glVertex2f(0, Constants.WORLD_HEIGHT);
        glEnd();

        // "DESTROYED" text area
        float centerX = Constants.WORLD_WIDTH / 2f;
        float centerY = Constants.WORLD_HEIGHT / 2f;
        
        glBegin(GL_QUADS);
        glColor4f(0.8f, 0.1f, 0.1f, 0.9f);
        glVertex2f(centerX - 150, centerY - 30);
        glVertex2f(centerX + 150, centerY - 30);
        glVertex2f(centerX + 150, centerY + 30);
        glVertex2f(centerX - 150, centerY + 30);
        glEnd();

        glLineWidth(3.0f);
        glBegin(GL_LINE_LOOP);
        glColor4f(1.0f, 0.3f, 0.3f, 1.0f);
        glVertex2f(centerX - 150, centerY - 30);
        glVertex2f(centerX + 150, centerY - 30);
        glVertex2f(centerX + 150, centerY + 30);
        glVertex2f(centerX - 150, centerY + 30);
        glEnd();

        // Respawning text
        glBegin(GL_QUADS);
        glColor4f(0.3f, 0.3f, 0.3f, 0.9f);
        glVertex2f(centerX - 80, centerY + 50);
        glVertex2f(centerX + 80, centerY + 50);
        glVertex2f(centerX + 80, centerY + 70);
        glVertex2f(centerX - 80, centerY + 70);
        glEnd();
    }

    public void renderKillFeed(List<String> killFeed, List<Float> timers) {
        float x = Constants.WORLD_WIDTH - 250;
        float y = 150;
        float lineHeight = 25;

        for (int i = 0; i < killFeed.size(); i++) {
            float alpha = Math.min(timers.get(i) / 2.0f, 1.0f);
            
            // Background
            glBegin(GL_QUADS);
            glColor4f(0.0f, 0.0f, 0.0f, 0.6f * alpha);
            glVertex2f(x, y + i * lineHeight);
            glVertex2f(x + 230, y + i * lineHeight);
            glVertex2f(x + 230, y + i * lineHeight + 20);
            glVertex2f(x, y + i * lineHeight + 20);
            glEnd();

            // Kill indicator
            glBegin(GL_QUADS);
            glColor4f(1.0f, 0.3f, 0.3f, alpha);
            glVertex2f(x + 5, y + i * lineHeight + 5);
            glVertex2f(x + 15, y + i * lineHeight + 5);
            glVertex2f(x + 15, y + i * lineHeight + 15);
            glVertex2f(x + 5, y + i * lineHeight + 15);
            glEnd();
        }
    }

    private void drawCompass(float x, float y, float radius, float rotation) {
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(0.1f, 0.1f, 0.15f, 0.8f);
        glVertex2f(x, y);
        for (int i = 0; i <= 32; i++) {
            float angle = (float) (i * Math.PI * 2 / 32);
            glVertex2f(x + (float) Math.cos(angle) * radius,
                       y + (float) Math.sin(angle) * radius);
        }
        glEnd();

        glLineWidth(2.0f);
        glBegin(GL_LINE_LOOP);
        glColor3f(0.5f, 0.5f, 0.6f);
        for (int i = 0; i < 32; i++) {
            float angle = (float) (i * Math.PI * 2 / 32);
            glVertex2f(x + (float) Math.cos(angle) * radius,
                       y + (float) Math.sin(angle) * radius);
        }
        glEnd();

        glPushMatrix();
        glTranslatef(x, y, 0);
        glRotatef((float) Math.toDegrees(rotation), 0, 0, 1);
        glBegin(GL_TRIANGLES);
        glColor3f(1.0f, 0.3f, 0.3f);
        glVertex2f(radius * 0.8f, 0);
        glVertex2f(-radius * 0.3f, -radius * 0.3f);
        glVertex2f(-radius * 0.3f, radius * 0.3f);
        glEnd();
        glPopMatrix();
    }

    private void drawPlayerCount(float x, float y, int count) {
        glBegin(GL_QUADS);
        glColor4f(0.2f, 0.2f, 0.3f, 0.8f);
        glVertex2f(x, y);
        glVertex2f(x + 80, y);
        glVertex2f(x + 80, y + 20);
        glVertex2f(x, y + 20);
        glEnd();

        for (int i = 0; i < count && i < 8; i++) {
            float iconX = x + 10 + i * 10;
            glBegin(GL_TRIANGLES);
            glColor3f(0.2f, 0.8f, 1.0f);
            glVertex2f(iconX + 4, y + 10);
            glVertex2f(iconX - 2, y + 16);
            glVertex2f(iconX - 2, y + 4);
            glEnd();
        }
    }

    private void drawFPSIndicator(float x, float y, int fps) {
        float width = 50;
        float height = 15;
        
        glBegin(GL_QUADS);
        glColor4f(0.1f, 0.1f, 0.1f, 0.7f);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();

        if (fps >= 55) {
            glColor3f(0.2f, 1.0f, 0.2f);
        } else if (fps >= 30) {
            glColor3f(1.0f, 1.0f, 0.2f);
        } else {
            glColor3f(1.0f, 0.2f, 0.2f);
        }

        float fpsRatio = Math.min(fps / 60.0f, 1.0f);
        glBegin(GL_QUADS);
        glVertex2f(x + 2, y + 2);
        glVertex2f(x + 2 + (width - 4) * fpsRatio, y + 2);
        glVertex2f(x + 2 + (width - 4) * fpsRatio, y + height - 2);
        glVertex2f(x + 2, y + height - 2);
        glEnd();
    }
}