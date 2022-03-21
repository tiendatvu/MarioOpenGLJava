package components;

import org.joml.Vector2f;

public abstract class Collider extends Component {
    /**
     * The distance from the BOTTOM LEFT of the box to the CENTER of it.
     * This is needed because the 3rd party Physics engine (Box2D)
     * draws box from the center of its.
     * Then it specifies all 4 vertices by add/minus half size of the box from the center.
     */
    protected Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return this.offset;
    }
}