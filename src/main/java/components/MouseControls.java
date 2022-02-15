package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;

    /**
     * Click on an sprite
     * => Assign it as the object on hold
     * => Add the object to the scene
     * @param go
     */
    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    /**
     * Release the pointer to the held object
     */
    public void place() {
        this.holdingObject = null;
    }

    /**
     * - Update the position of the held object from time to time
     * - If the left button clicked -> place the object and then release the control
     * @param dt
     */
    @Override
    public void update(float dt) {
        if (holdingObject != null) {
            // As we known for this situation, each sprite size is (16, 16)
            // (0, 0) is bottom left of a sprite
            // (-16, -16) to get to the middle area of the sprite after scaling the sprite
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                //System.out.println("left clicked: ");
            }
        }
    }
}
