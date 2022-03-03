package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    /**
     * singleton pattern
     * @return
     */
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE)
        {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    /**
     * Reset the scroll value/position value at each end of a loop in Window
     */
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    /**
     * Get the current position of mouse inside of the Game Viewport (GV)
     * The Game Viewport is a small portion on the whole screen
     * This functions is called when the mouse position is inside of the GV
     * @return
     */
    public static float getScreenX() {
        // Get the relative position of the mouse cursor when the it is inside the GV
        float currentX = getX() - get().gameViewportPos.x;
        // Because the framebuffer loading for the GV is set to Window.Size,
        // then it would be shrunk down to the size of the actual GV.
        // If we want to consider any pixel inside of the GV,
        // we should convert the coordinates back to the Window.Size
        currentX = (currentX / get().gameViewportSize.x) * Window.getWidth();
        return currentX;
    }

    public static float getScreenY() {
        float currentY = getY() - get().gameViewportPos.y;
        currentY = Window.getHeight() - ((currentY / get().gameViewportSize.y) * Window.getHeight());
        return currentY;
    }

    /**
     * @return The mouse position.x in world coordinates
     */
    public static float getOrthoX() {
        // Consider x coordinate inside of the game view port
        // - before:
        // currentX = getX();
        float currentX = getX() - get().gameViewportPos.x;
        // Normalize the coordinate
        // - before: normalize by the whole display window width
        // currentX = (currentX / (float)Window.getWidth()) * 2.0f - 1.0f;
        // - current: normalize by the game view port size
        // currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        // Get the mouse position into the vector
        // In this case, we consider 2D -> x,y. but the math works with 3D (z coordinate)
        // and w to keep the
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        // Translate the mouse position into the world coordinates
        // It also depends on the View Matrix and Projection Matrix defined in class Camera
        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);
        currentX = tmp.x;
        return currentX;
    }

    /**
     * @return The mouse position.y in world coordinates
     */
    public static float getOrthoY() {
        float currentY = getY() - get().gameViewportPos.y;
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);
        currentY = tmp.y;
        return currentY;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }
}