package jade;

import components.Sprite;
import components.SpriteRenderer;
import org.joml.Vector2f;

import java.util.Vector;

public class Prefabs {
    /**
     * Generate a GameObject from the blocked selected
     * @param sprite displayed image
     * @param sizeX size x to be displayed on screen
     * @param sizeY size y to be displayed on screen
     * @return
     */
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}
