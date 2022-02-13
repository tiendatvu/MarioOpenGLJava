package components;

import jade.GameObject;

// Like interface in c++. force implementation to do some abstract methods
public abstract class Component {
    /**
     * - Show the component is currently belonged to which GameObject on the screen
     * - Different set of Components should form different type of GameObject
     * - GameObject is transient in the class Component because:
     *   + serialize GameObject is serializing Components
     *   + a Component contains the pointer to "mother" GameObject
     *   -> If serialize GameObject inside of Component -> cause the infinite loop
     */
    public transient GameObject gameObject = null;

    // override to rewrite
    public void start() {

    }

    // override if needed
    public void update(float dt) {

    }

    public void imgui() {

    }
}
