package editor;

import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class PropertiesWindow {
    // Show what GameObject is being modified
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    /**
     * Time counter to 0 -> check if the user click and hold the button long enough.
     * Help to reduce number of checking times
     */
    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;
        // Check debounce counter to reduce checking times
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectId);
            this.debounce = 0.2f;
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObject;
    }
}
