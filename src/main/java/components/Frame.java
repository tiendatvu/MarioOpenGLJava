package components;

/**
 * Contain the displayed sprite and the amount of time to display it
 */
public class Frame {
    public Sprite sprite;
    public float frameTime;

    public Frame() {

    }

    public Frame(Sprite sprite, float frameTime) {
        this.sprite = sprite;
        this.frameTime = frameTime;
    }
}
