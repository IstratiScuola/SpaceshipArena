package client.graphics;

import static org.lwjgl.opengl.GL11.*;

//codice preso da documentazione trovata online
public class BitmapFont {

    private static final float CHAR_WIDTH = 8;
    private static final float CHAR_HEIGHT = 12;
    private static final float CHAR_SPACING = 2;

    /**
     * Draw text at the specified position
     */
    public static void drawText(String text, float x, float y, float scale, float r, float g, float b, float a) {
        glColor4f(r, g, b, a);

        float cursorX = x;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            drawChar(c, cursorX, y, scale);
            cursorX += (CHAR_WIDTH + CHAR_SPACING) * scale;
        }
    }

    /**
     * Draw text centered at the specified position
     */
    public static void drawTextCentered(String text, float centerX, float y, float scale, float r, float g, float b,
            float a) {
        float totalWidth = text.length() * (CHAR_WIDTH + CHAR_SPACING) * scale;
        drawText(text, centerX - totalWidth / 2, y, scale, r, g, b, a);
    }

    /**
     * Get the width of a text string at the given scale
     */
    public static float getTextWidth(String text, float scale) {
        return text.length() * (CHAR_WIDTH + CHAR_SPACING) * scale;
    }

    private static void drawChar(char c, float x, float y, float scale) {
        float w = CHAR_WIDTH * scale;
        float h = CHAR_HEIGHT * scale;

        c = Character.toUpperCase(c);

        glLineWidth(2.0f * scale);

        // Note: Screen coordinates - Y=0 at top, increases downward
        // Character is drawn from top-left (x, y) to bottom-right (x+w, y+h)
        switch (c) {
            case 'A':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h); // bottom left
                glVertex2f(x + w / 2, y); // top center
                glVertex2f(x + w, y + h); // bottom right
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.25f, y + h * 0.6f);
                glVertex2f(x + w * 0.75f, y + h * 0.6f);
                glEnd();
                break;
            case 'B':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w, y + h * 0.25f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x, y + h * 0.5f);
                glEnd();
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.75f);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x, y + h);
                glEnd();
                break;
            case 'C':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w, y + h * 0.8f);
                glEnd();
                break;
            case 'D':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.6f, y);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w * 0.6f, y + h);
                glVertex2f(x, y + h);
                glEnd();
                break;
            case 'E':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y);
                glVertex2f(x, y);
                glVertex2f(x, y + h);
                glVertex2f(x + w, y + h);
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glEnd();
                break;
            case 'F':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y);
                glVertex2f(x, y);
                glVertex2f(x, y + h);
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glEnd();
                break;
            case 'G':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w, y + h * 0.8f);
                glVertex2f(x + w, y + h * 0.5f);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glEnd();
                break;
            case 'H':
                glBegin(GL_LINES);
                glVertex2f(x, y);
                glVertex2f(x, y + h);
                glVertex2f(x + w, y);
                glVertex2f(x + w, y + h);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.5f);
                glEnd();
                break;
            case 'I':
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glEnd();
                break;
            case 'J':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w * 0.7f, y);
                glEnd();
                break;
            case 'K':
                glBegin(GL_LINES);
                glVertex2f(x, y);
                glVertex2f(x, y + h);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w, y);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case 'L':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x, y + h);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case 'M':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w, y);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case 'N':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w, y + h);
                glVertex2f(x + w, y);
                glEnd();
                break;
            case 'O':
                glBegin(GL_LINE_LOOP);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.7f, y);
                glEnd();
                break;
            case 'P':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w, y + h * 0.25f);
                glVertex2f(x + w, y + h * 0.45f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x, y + h * 0.5f);
                glEnd();
                break;
            case 'Q':
                glBegin(GL_LINE_LOOP);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.7f, y);
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.6f, y + h * 0.7f);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case 'R':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w, y + h * 0.25f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x, y + h * 0.5f);
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case 'S':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y + h * 0.2f);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x + w * 0.3f, y + h * 0.5f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x, y + h * 0.8f);
                glEnd();
                break;
            case 'T':
                glBegin(GL_LINES);
                glVertex2f(x, y);
                glVertex2f(x + w, y);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x + w * 0.5f, y + h);
                glEnd();
                break;
            case 'U':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w, y);
                glEnd();
                break;
            case 'V':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w, y);
                glEnd();
                break;
            case 'W':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.25f, y + h);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w * 0.75f, y + h);
                glVertex2f(x + w, y);
                glEnd();
                break;
            case 'X':
                glBegin(GL_LINES);
                glVertex2f(x, y);
                glVertex2f(x + w, y + h);
                glVertex2f(x + w, y);
                glVertex2f(x, y + h);
                glEnd();
                break;
            case 'Y':
                glBegin(GL_LINES);
                glVertex2f(x, y);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w, y);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w * 0.5f, y + h);
                glEnd();
                break;
            case 'Z':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x + w, y);
                glVertex2f(x, y + h);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case '0':
                glBegin(GL_LINE_LOOP);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.7f, y);
                glEnd();
                break;
            case '1':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w * 0.3f, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x + w * 0.5f, y + h);
                glEnd();
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.2f, y + h);
                glVertex2f(x + w * 0.8f, y + h);
                glEnd();
                break;
            case '2':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h * 0.2f);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w, y + h * 0.5f);
                glVertex2f(x, y + h);
                glVertex2f(x + w, y + h);
                glEnd();
                break;
            case '3':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.5f, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x, y + h * 0.8f);
                glEnd();
                break;
            case '4':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w, y + h * 0.7f);
                glEnd();
                break;
            case '5':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y);
                glVertex2f(x, y);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x, y + h);
                glEnd();
                break;
            case '6':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w, y + h * 0.5f);
                glVertex2f(x, y + h * 0.5f);
                glEnd();
                break;
            case '7':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y);
                glVertex2f(x + w, y);
                glVertex2f(x + w * 0.3f, y + h);
                glEnd();
                break;
            case '8':
                glBegin(GL_LINE_LOOP);
                glVertex2f(x + w * 0.3f, y + h * 0.5f);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                glVertex2f(x + w * 0.3f, y + h * 0.5f);
                glVertex2f(x, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glVertex2f(x + w * 0.7f, y + h);
                glVertex2f(x + w, y + h * 0.7f);
                glVertex2f(x + w * 0.7f, y + h * 0.5f);
                glEnd();
                break;
            case '9':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x, y + h * 0.8f);
                glVertex2f(x + w * 0.5f, y + h);
                glVertex2f(x + w, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.3f);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x, y + h * 0.3f);
                glVertex2f(x, y + h * 0.5f);
                glVertex2f(x + w, y + h * 0.5f);
                glEnd();
                break;
            case ' ':
                // Space - no drawing needed
                break;
            case ':':
                glBegin(GL_QUADS);
                glVertex2f(x + w * 0.4f, y + h * 0.25f);
                glVertex2f(x + w * 0.6f, y + h * 0.25f);
                glVertex2f(x + w * 0.6f, y + h * 0.35f);
                glVertex2f(x + w * 0.4f, y + h * 0.35f);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(x + w * 0.4f, y + h * 0.65f);
                glVertex2f(x + w * 0.6f, y + h * 0.65f);
                glVertex2f(x + w * 0.6f, y + h * 0.75f);
                glVertex2f(x + w * 0.4f, y + h * 0.75f);
                glEnd();
                break;
            case '-':
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.2f, y + h * 0.5f);
                glVertex2f(x + w * 0.8f, y + h * 0.5f);
                glEnd();
                break;
            case '!':
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.5f, y);
                glVertex2f(x + w * 0.5f, y + h * 0.7f);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(x + w * 0.4f, y + h * 0.85f);
                glVertex2f(x + w * 0.6f, y + h * 0.85f);
                glVertex2f(x + w * 0.6f, y + h);
                glVertex2f(x + w * 0.4f, y + h);
                glEnd();
                break;
            case '(':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w * 0.7f, y);
                glVertex2f(x + w * 0.3f, y + h * 0.3f);
                glVertex2f(x + w * 0.3f, y + h * 0.7f);
                glVertex2f(x + w * 0.7f, y + h);
                glEnd();
                break;
            case ')':
                glBegin(GL_LINE_STRIP);
                glVertex2f(x + w * 0.3f, y);
                glVertex2f(x + w * 0.7f, y + h * 0.3f);
                glVertex2f(x + w * 0.7f, y + h * 0.7f);
                glVertex2f(x + w * 0.3f, y + h);
                glEnd();
                break;
            case '.':
                glBegin(GL_QUADS);
                glVertex2f(x + w * 0.4f, y + h * 0.85f);
                glVertex2f(x + w * 0.6f, y + h * 0.85f);
                glVertex2f(x + w * 0.6f, y + h);
                glVertex2f(x + w * 0.4f, y + h);
                glEnd();
                break;
            case '+':
                glBegin(GL_LINES);
                glVertex2f(x + w * 0.5f, y + h * 0.2f);
                glVertex2f(x + w * 0.5f, y + h * 0.8f);
                glVertex2f(x + w * 0.2f, y + h * 0.5f);
                glVertex2f(x + w * 0.8f, y + h * 0.5f);
                glEnd();
                break;
            default:
                // Draw a rectangle for unknown characters
                glBegin(GL_LINE_LOOP);
                glVertex2f(x, y);
                glVertex2f(x + w, y);
                glVertex2f(x + w, y + h);
                glVertex2f(x, y + h);
                glEnd();
                break;
        }
    }
}
