package components;

import jade.GameObject;

// Like interface in c++. force implementation to do some abstract methods
public abstract class Component {
    /**
     * Show the component is currently belonged to which GameObject on the screen
     */
    public GameObject gameObject = null;

    // override to rewrite
    public void start() {

    }

    // have to implement in implementation classes
    public abstract void update(float dt);
}
