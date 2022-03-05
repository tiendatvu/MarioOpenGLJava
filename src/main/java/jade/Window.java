package jade;

import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.AssetPool;

import java.awt.event.WindowListener;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private FrameBuffer framebuffer;
    private PickingTexture pickingTexture;

    public float r, g, b, a;
    private boolean fadeToBlack = false;

    // singleton
    private static Window window = null;

    private static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1017;
        this.title = "Mario";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    // window can only be created by calling this function
    // singleton pattern
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void window_size_callback(long window, int newWidth, int newHeight)
    {
        Window.setWidth(newWidth);
        Window.setHeight(newHeight);
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        //glfwSetWindowSizeCallback(glfwWindow, Window::window_size_callback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync (swap buffer)
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable blending and use the rule to get the blending values
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new FrameBuffer(get().width, get().height);
        this.pickingTexture = new PickingTexture(get().width, get().height);
        glViewport(0, 0, get().width, get().height);

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        // the init scene of Window
        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = (float)glfwGetTime();// Time.getTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting(); // enable writing to write the color to frame buffer

            glViewport(0, 0, get().width, get().height);
            glClearColor(0.0f,0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting(); // disable writing for safety
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            // in the range of 2 functions: bind() and unbind()
            // all the data is stored into a buffer which is off-screen
            this.framebuffer.bind();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            //this.framebuffer.bind();

            if (dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(dt);
                currentScene.render();
            }
            this.framebuffer.unbind(); // try to move the unbind to different position to see the result

            this.imguiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float)glfwGetTime();// Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(glfwWindow, true);
            }
        }

        currentScene.saveExit();
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static FrameBuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return (float)get().width / get().height;
    }
}
