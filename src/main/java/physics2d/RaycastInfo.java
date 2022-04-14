package physics2d;

import jade.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RaycastInfo implements RayCastCallback {
    /**
     * The fixture that the ray hits
     */
    public Fixture fixture;
    /**
     * The point where the ray hits
     */
    public Vector2f point;
    /**
     * The normal vector at the hit point (pháp tuyến)
     */
    public Vector2f normal;
    /**
     * The fraction of the distance along the vector that we hit ???
     */
    public float fraction;
    /**
     * true if hits something
     */
    public boolean hit;
    /**
     * The object hits the requesting object
     */
    public GameObject hitObject;
    /**
     * The object requesting the callback (the object is hit on which we has the view point)
     */
    private GameObject requestingObject;

    public RaycastInfo(GameObject obj) {
        fixture = null;
        point = new Vector2f();
        normal = new Vector2f();
        fraction = 0.0f;
        hit = false;
        hitObject = null;
        this.requestingObject = obj;
    }

    /**
     * The callback function that box2d calls when ray casting hits an object
     * @param fixture
     * @param point
     * @param normal
     * @param fraction
     * @return
     */
    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        // If the ray cast hit the requestingObject itself, return 1 to keep looking
        if (fixture.m_userData == requestingObject) {
            return 1;
        }
        this.fixture = fixture;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = fraction != 0;
        // we could assign userData as a GameObject because
        // we define inside the file Physics2D:
        // bodyDef.userData = rb.gameObject;
        // fixtureDef.userData = boxCollider.gameObject;
        // fixtureDef.userData = circleCollider.gameObject;
        this.hitObject = (GameObject) fixture.m_userData;

        return fraction;
    }
}
