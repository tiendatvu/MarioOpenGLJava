package physics2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * Wrapper for physics like Render class for rendering
 */
public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f/60.0f;
    // the amount of iteration to resolve collision.
    // Need to read hello world from Box2d.org
    // -> documentation -> hello box2d -> creating a world
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public void update(float dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }
}
