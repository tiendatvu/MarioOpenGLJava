package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.MouseListener;
import jade.Prefabs;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {
    // Color of the arrow X and Y
    private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    // Consider each axis as an arrow, load and add the GameObject into the Current Scene
    private GameObject xAxisObject;
    private GameObject yAxisObject;

    // Helps drawing the GameObject axis
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;

    // The active game object showing the Gizmos
    protected GameObject activeGameObject = null;

    // Offset from the origin points for the arrows
    private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
    private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);

    private float gizmoWidth = 16f / 80f;
    private float gizmoHeight = 48f / 80f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        // Generate axis from the sprite
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        // Get the SpriteRenderer of the GameObject to draw the arrow later
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        // Get the current propertiesWindow JUST to get the active GameObject
        // If the active GameObject is not null -> showing the Gizmos system
        this.propertiesWindow = propertiesWindow;

        // TODO: not know the use
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        // Add 2 arrows to the scene
        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start() {
        // init the rotation of x axis and y axis
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        // set the zIndex of axes. Pick up and place a new object (sprite) into the scene.
        // This would help the X and Y axis placed front of the sprite
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        // Not save the axis into file
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        // TODO: why if using the Gizmo system, inactivate the objects in the scene
        if (using) {
            this.setInactive();
        }
        this.xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) return;

        // Get the current propertiesWindow JUST to get the active GameObject
        // If the active GameObject is not null -> showing the Gizmos system
        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null) {
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) &&
                MouseListener.isDragging() &&
                MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) &&
                MouseListener.isDragging() &&
                MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = false;
            yAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        // If user clicks on an object -> the active game object != null
        // -> set the position of the 2 arrows:
        // + the position of the active game object
        // + the offset value of the arrow which are set above
        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    private void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    /**
     * Check if the mouse is hovering inside of the horizontal axis
     * @return
     */
    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        // Because the xAxis is rotated 90, the origin of the arrow is its pointer
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoHeight / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoWidth / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    /**
     * Check if the mouse is hovering inside of the vertical axis
     * @return
     */
    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        // Because the yAxis is rotated 180
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    /**
     * set to use in TranslateGizmo and ScaleGizmo classes.
     * The user should use translate or scale or nothing (switch among 3 cases)
     */
    public void setUsing() {
        this.using = true;
    }

    /**
     * set to not use translate or scale
     */
    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}
