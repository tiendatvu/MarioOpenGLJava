package jade;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        // init the position of the camera
        this.camera = new Camera(new Vector2f(-250, 0));

        float xOffset = 0;
        float xDelta = 60;
        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage.png")));
        this.addGameObjectToScene(obj1);
        xOffset += xDelta;

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage2.png")));
        this.addGameObjectToScene(obj2);
        xOffset += xDelta;

        GameObject obj3 = new GameObject("Object 3", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj3.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage3.png")));
        this.addGameObjectToScene(obj3);
        xOffset += xDelta;

        GameObject obj4 = new GameObject("Object 4", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj4.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage4.png")));
        this.addGameObjectToScene(obj4);
        xOffset += xDelta;

        GameObject obj5 = new GameObject("Object 5", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj5.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage5.png")));
        this.addGameObjectToScene(obj5);
        xOffset += xDelta;

        GameObject obj6 = new GameObject("Object 6", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj6.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage6.png")));
        this.addGameObjectToScene(obj6);
        xOffset += xDelta;

        GameObject obj7 = new GameObject("Object 7", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj7.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage7.png")));
        this.addGameObjectToScene(obj7);
        xOffset += xDelta;

        // TODO: when the number of textured loaded is exceeded from the length of texslot
        //       -> need to render a new batch to add a new texture into it
        //       Here, from the obj8, the texture is not loaded
        GameObject obj8 = new GameObject("Object 8", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj8.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage8.png")));
        this.addGameObjectToScene(obj8);
        xOffset += xDelta;

        GameObject obj9 = new GameObject("Object 9", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj9.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage9.png")));
        this.addGameObjectToScene(obj9);
        xOffset += xDelta;

        GameObject obj10 = new GameObject("Object 10", new Transform(new Vector2f(xOffset, 0), new Vector2f(50, 50)));
        obj10.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage10.png")));
        this.addGameObjectToScene(obj10);

        loadResources();
    }

    private void loadResources() {
        // Load to init/compile the shader files
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        System.out.println("dt: " + dt);
        System.out.println("FPS: " + (1.0 / dt));

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
}
