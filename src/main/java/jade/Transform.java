package jade;

import org.joml.Vector2f;

import java.util.Objects;

/**
 * This should describe a PRIMITIVE (like TRIANGLE, QUAD, LINE...)
 */
public class Transform {

    /**
     * The original position of the vertex (usually the bottom-left of a QUAD)
     */
    public Vector2f position;
    /**
     * EDGE SIZE of the QUAD
     */
    public Vector2f scale;

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
    }

    /**
     * Return the deep copy record of this Transform
     * @return
     */
    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
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
        return Objects.equals(position, transform.position) && Objects.equals(scale, transform.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, scale);
    }
}
