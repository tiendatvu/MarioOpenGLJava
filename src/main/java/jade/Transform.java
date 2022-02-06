package jade;

import org.joml.Vector2f;

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
}
