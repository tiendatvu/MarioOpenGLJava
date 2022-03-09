package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj1Sprite;

    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        // Init the camera (with default position, not loaded from ini file)
        this.camera = new Camera(new Vector2f(-250, 0));
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        // get the sprite from the sprite sheet with its index: gizmos.getSprite(1)
        levelEditorStuff.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getImGuiLayer().getPropertiesWindow()));

        levelEditorStuff.addComponent(new GizmoSystem((gizmos)));

        levelEditorStuff.start(); // Init components of the GameObject

//        obj1 = new GameObject("Object 1",
//                              new Transform(new Vector2f(200, 100),
//                              new Vector2f(256, 256)), 2);
//        obj1Sprite = new SpriteRenderer();
//        obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
//        obj1.addComponent(obj1Sprite);
//        obj1.addComponent(new Rigidbody());
//        this.addGameObjectToScene(obj1);
//        this.activeGameObject = obj1;
//
//        GameObject obj2 = new GameObject("Object 2",
//                                         new Transform(new Vector2f(400, 100),
//                                         new Vector2f(256, 256)), 3);
//        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
//        Sprite obj2Sprite = new Sprite();
//        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
//        obj2SpriteRenderer.setSprite(obj2Sprite);
//        obj2.addComponent(obj2SpriteRenderer);
//        this.addGameObjectToScene(obj2);
//
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .create();
//        String serialized = gson.toJson(obj1);
//        System.out.println(serialized);
//        GameObject obj = gson.fromJson(serialized, GameObject.class);
//        System.out.println(obj);
    }

    private void loadResources() {
        // Load to init/compile the shader files
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                                 new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                                 16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");

        // The variable texID (Texture Id) inside Texture class is set to transient
        // The system cannot save and load the texID each restart time.
        // Each sprite has its own file path to load the texture based on texCoords (coordinates)
        // -> Use AssetPool to [Create new texture/Load existing texture] by file path
        for (GameObject g : gameObjects) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }

    float t = 0.0f;
    float angle = 0.0f;
    float x = 0.0f;
    float y = 0.0f;
    @Override
    public void update(float dt) {
        //System.out.println("FPS: " + (1.0 / dt));

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

        levelEditorStuff.update(dt);
        this.camera.adjustProjection();

//        DebugDraw.addBox2D(new Vector2f(200, 200), new Vector2f(63, 32), angle, new Vector3f(0, 1, 0));
//        angle += 40 * dt;
//
//        DebugDraw.addCircle(new Vector2f(x, y), 64, new Vector3f(0, 1, 0), 1);
//        x += 50.0f * dt;
//        y += 50.0f * dt;

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

    @Override
    public void render() {
        this.renderer.render();
    }

    /**
     * Do whatever imgui need to do/display/change the components
     */
    @Override
    public void imgui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        // Just show how to display an imGui window
        ImGui.begin("Test window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float scale = 2.0f;
            float spriteWidth = sprite.getWidth() * scale; // width displayed on the screen
            float spriteHeight = sprite.getHeight() * scale; // height displayed on the screen
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            // TODO:
            //  try different texCoords composition
            //  1. texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y -> correct
            //  2. texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y -> flip
            //  and the getOrthoY() in the MouseListener
            if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                    texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            // Check if there are still sprite in the list and
            // The width of the next sprite is not exceeded from the windowX2.x
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
