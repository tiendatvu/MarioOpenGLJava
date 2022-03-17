package observers;

import jade.GameObject;
import observers.events.Event;

/**
 * Force the implementation to Override the function onNotify().
 * This would notify an object when an event takes place
 */
public interface Observer {
    void onNotify(GameObject object, Event event);
}
