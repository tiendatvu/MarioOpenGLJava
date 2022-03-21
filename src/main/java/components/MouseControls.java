package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import util.Settings;

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
    public void editorUpdate(float dt) {
        if (holdingObject != null) {
            // Snap the position of the object picked into grid line
            holdingObject.transform.position.x = MouseListener.getOrthoX();
            holdingObject.transform.position.y = MouseListener.getOrthoY();
            // - Get the number of the grid line the object should be placed in, based on the mouse position
            // (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH)
            // - Specify the position in the WORLD coordinates
            // (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH:
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
                //System.out.println("left clicked: ");
            }
        }
    }
}
