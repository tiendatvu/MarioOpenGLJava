package physics2d.components;

import components.Collider;

/**
 *
 */
public class CircleCollider extends Collider {
    private float radius = 1f;

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
