package jade;

import components.Component;
import editor.JImGui;
import org.joml.Vector2f;

import java.util.Objects;

/**
 * This should describe a PRIMITIVE (like TRIANGLE, QUAD, LINE...)
 */
public class Transform extends Component {

    /**
     * The original position of the vertex (usually the bottom-left of a QUAD)
     */
    public Vector2f position;
    /**
     * EDGE SIZE of the QUAD
     */
    public Vector2f scale;
    /**
     * Rotation of the sprite
     */
    public float rotation = 0.0f;
    /**
     * Init the z index. This should decide which batch the sprite should go into,
     * and the order to be drawn
     */
    public int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    /**
     *
     * @param position original position of the vertex (usually the bottom-left of a QUAD)
     * @param scale EDGE SIZE of the QUAD
     */
    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.zIndex = 0;
    }

    /**
     * Return the deep copy record of this Transform
     * @return
     */
    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    @Override
    public void imgui() {
        gameObject.name = JImGui.inputText("Name: ", gameObject.name);
        JImGui.drawVec2Control("Position", this.position);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = JImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = JImGui.dragInt("ZIndex", this.zIndex);
    }

    /**
     * Copy this Transform to another record
     * @param to
     */
    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transform transform = (Transform) o;
        return transform.position.equals(this.position) &&
                transform.scale.equals(this.scale) &&
                transform.rotation == this.rotation &&
                transform.zIndex == this.zIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, scale);
    }
}
