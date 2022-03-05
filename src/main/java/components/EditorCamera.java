package components;

import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component{

    /**
     * 60fps -> 1 frame takes 1/60= 0.016
     *       -> 0.032 jumps 2 frames
     */
    private float dragDebounce = 0.032f;
    /**
     * The scene's camera. This should be edited while user move cursor around or zoom in/out
     */
    private Camera levelEditorCamera;
    /**
     * The start position of mouse cursor when it is clicked
     */
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scroolSensitivity = 0.1f;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt) {
        //------------------------------------------DRAGGING------------------------------------------//
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            // If the button is not hold down for 2 frames,
            // just update the position of the first place clicking.
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            // Current mouse position
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            // Get the distance between the position start dragging and current position of the mouse
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            // Update the camera position
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }

        // Reset the time counter for dragging
        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.032f;
        }

        //------------------------------------------SCROLLING------------------------------------------//
        // Check if mouse scroll Y
        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scroolSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        //------------------------------------------RESET TO ORIGINAL-----------------------------------//
        // If the DOT is pressed, get back to the original status
        // We do this, because the key would be pressed multiple frames
        // -> do this to reset only one time with the reset flag
        //if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
        if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) ||
             KeyListener.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) &&
            KeyListener.isKeyPressed(GLFW_KEY_R)) {
            reset = true;
        }

        if (reset) {
            // LERP from the current position to (0, 0)
            // This means: from the current position,
            // move a distance = (x_des - x_src) * lerpTime
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            // Lerp (linear interpolate) the camera's zoom ratio till zoom = 1
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            // Increase the lerpTime to increase the lerp pace
            this.lerpTime += 0.1f * dt;

            // Check if the position of the camera reaches the threshold,
            // reset all properties to the default.
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
            Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
