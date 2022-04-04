package components;

import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private float time = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = false;

    /**
     * Reload/re-create id for a texture of frames.
     * - When loading texture from file: Texture Id (texId is transient -> not saved in the file level.txt)
     * - When placing new Object (having states) into scene: clone object using gson -> need to create new texture id
     */
    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilePath()));
        }
    }

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new Frame(sprite, frameTime));
        }
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    /**
     * Loop through the sprite list to create dynamic animation (walk, run, jump,...)
     * @param dt
     */
    public void update(float dt) {
        if (currentSprite < animationFrames.size()) {
            time -= dt;
            if (time <= 0) {
                //
                //if (!(currentSprite == animationFrames.size() - 1 && !doesLoop)) {
                if (currentSprite != animationFrames.size() - 1 || doesLoop) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                time = animationFrames.get(currentSprite).frameTime;
            }
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }
}
