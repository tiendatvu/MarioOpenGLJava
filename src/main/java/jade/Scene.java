package jade;

import imgui.ImGui;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;

    public Scene() {

    }

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            // dynamically adding game object to the scene
            // start the newly added game object when the scene is running
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }

    /**
     * Display an inspector for an element on the scene
     */
    public void sceneImgui() {
        if (activeGameObject != null) {
            ImGui.begin("sceneImgui Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    /**
     * Should be overridden in the implemented Scene
     */
    public void imgui() {

    }
}