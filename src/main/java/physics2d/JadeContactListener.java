package physics2d;

import components.Component;
import imgui.callback.ImPlatformFuncViewportImVec2;
import jade.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * The order of collision:
 * - preSolve
 * - beginContact
 * - endContact
 * - postSolve
 */
public class JadeContactListener implements ContactListener {
    /**
     * A begin contact with B
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {
        // we could assign userData as a GameObject because
        // we define inside the file Physics2D:
        // bodyDef.userData = rb.gameObject;
        // fixtureDef.userData = boxCollider.gameObject;
        // fixtureDef.userData = circleCollider.gameObject;
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        // WorldManifold is Collision information
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold); // get the world coordinates where collision happens
        // the direction other object is hitting the object A
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        // the direction other object is hitting the object B
        // B fixture is acting at the opposite direction compared to the A fixture
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.beginCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.beginCollision(objA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.endCollision(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.endCollision(objA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.preSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.preSolve(objA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c : objA.getAllComponents()) {
            c.postSolve(objB, contact, aNormal);
        }

        for (Component c : objB.getAllComponents()) {
            c.postSolve(objA, contact, bNormal);
        }
    }
}
