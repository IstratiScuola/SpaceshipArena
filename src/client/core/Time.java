package client.core;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    private static double lastTime = 0;
    private static float deltaTime = 0;
    private static int fps = 0;
    private static int frameCount = 0;
    private static double fpsTimer = 0;

    private static double targetFPS = 60.0;
    private static double targetFrameTime = 1.0 / targetFPS;
    private static double lastFrameTime = 0;
    private static boolean fpsLimitEnabled = true;

    public static void init() {
        lastTime = glfwGetTime();
        lastFrameTime = glfwGetTime();
    }

    public static void update() {
        double currentTime = glfwGetTime();
        deltaTime = (float) (currentTime - lastTime);
        lastTime = currentTime;

        frameCount++;
        fpsTimer += deltaTime;
        if (fpsTimer >= 1.0) {
            fps = frameCount;
            frameCount = 0;
            fpsTimer -= 1.0;
        }
    }

    public static void sync() {
        if (!fpsLimitEnabled) return;

        double targetTime = lastFrameTime + targetFrameTime;
        double currentTime = glfwGetTime();

        while (currentTime < targetTime) {
            double sleepTime = targetTime - currentTime;
            if (sleepTime > 0.002) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentTime = glfwGetTime();
        }

        lastFrameTime = glfwGetTime();
    }

    public static float getDeltaTime() { return deltaTime; }
    public static int getFPS() { return fps; }
    
    public static void setTargetFPS(double fps) {
        targetFPS = fps;
        targetFrameTime = 1.0 / fps;
    }
}