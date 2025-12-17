package client.core;

import static org.lwjgl.glfw.GLFW.*;
//questo codice è stato direttamente importato dalle documentazioni di glfw
public class Input {
    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];

    public Input(Window window) {
        glfwSetKeyCallback(window.getHandle(), (win, key, scancode, action, mods) -> {
            if (key >= 0 && key <= GLFW_KEY_LAST) {
                keys[key] = (action == GLFW_PRESS || action == GLFW_REPEAT);
            }
        });
    }

    public boolean isKeyDown(int key) {
        return key >= 0 && key <= GLFW_KEY_LAST && keys[key];
    }
}