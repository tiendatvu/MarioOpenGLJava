package jade;

import components.Sprite;
import components.SpriteRenderer;
import org.joml.Vector2f;

import java.util.Vector;

public class Prefabs {
    /**
     * Generate a GameObject from the blocked selected
     * @param sprite
     * @param sizeX
     * @param sizeY
     * @return
     */
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), 0);
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
