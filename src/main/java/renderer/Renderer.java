package renderer;

import components.SpriteRenderer;
import jade.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    /**
     * Loop through existed batches -> check if any has room for a new sprite
     * -> add the sprite into it || create a new batch and then add the sprite into the new one.
     * @param sprite
     */
    private void add(SpriteRenderer sprite) {
        boolean added = false;

        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }

        // If there's no room for adding a new sprite -> create a new batch to add into
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
