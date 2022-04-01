package components;

import jade.GameObject;
import jade.KeyListener;
import jade.MouseListener;
import jade.Window;
import org.joml.Vector4f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.05f;
    private float debounce = debounceTime;

    /**
     * Click on an sprite
     * => Assign it as the object on hold
     * => Add the object to the scene
     * @param go
     */
    public void pickupObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    /**
     * Release the pointer to the held object
     */
    public void place() {
        GameObject newObj = holdingObject.copy();
//        if (newObj.getComponent(StateMachine.class) != null) {
//            newObj.getComponent(StateMachine.class).refreshTextures();
//        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        newObj.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(newObj);
    }

    /**
     * - Update the position of the held object from time to time
     * - If the left button clicked -> place the object and then release the control
     * @param dt
     */
    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        if (holdingObject != null && debounce <= 0.0f) {
            // Snap the position of the object picked into grid line
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            // - Get the number of the grid line the object should be placed in, based on the mouse position
            // (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH)
            // - Specify the position in the WORLD coordinates
            // (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH:
            holdingObject.transform.position.x = ((int)Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) +
                                                 Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) +
                                                 Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                debounce = debounceTime;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }
}
