package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj1Sprite;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));
        if (this.levelLoaded) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1",
                              new Transform(new Vector2f(200, 100),
                              new Vector2f(256, 256)), 2);
        obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Sprite);
        obj1.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2",
                                         new Transform(new Vector2f(400, 100),
                                         new Vector2f(256, 256)), 3);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String serialized = gson.toJson(obj1);
        System.out.println(serialized);
//        GameObject obj = gson.fromJson(serialized, GameObject.class);
//        System.out.println(obj);
    }

    private void loadResources() {
        // Load to init/compile the shader files
        AssetPool.getShader("assets/shaders/default.glsl");

        // TODO: FIX TEXTURE SAVE SYSTEM TO USE PATH INSTEAD OF ID
        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                                 new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                                 16, 16, 26, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");
    }

    @Override
    public void update(float dt) {
//        System.out.println("dt: " + dt);
//        System.out.println("FPS: " + (1.0 / dt));

        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.position.x += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            camera.position.x -= 100f * dt;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            camera.position.y += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.position.y -= 100f * dt;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    /**
     * Do whatever imgui need to do/display/change the components
     */
    @Override
    public void imgui() {
        // Just show how to display an imGui window
        ImGui.begin("Test window");
        ImGui.text("Some random text");
        ImGui.end();
    }
}
