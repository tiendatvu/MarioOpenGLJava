package jade;

import components.*;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

import javax.swing.plaf.nimbus.State;
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

    public static GameObject generateMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0),
                Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

        AnimationState run = new AnimationState();
        run.title = "run";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/items.png");
        GameObject questionBlock = generateSpriteObject(playerSprites.getSprite(0),
                Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

        AnimationState run = new AnimationState();
        run.title = "Question";
        float defaultFrameTime = 0.23f;
        run.addFrame(playerSprites.getSprite(0), 0.57f);
        run.addFrame(playerSprites.getSprite(1), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }
}
