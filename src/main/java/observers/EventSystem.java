package observers;

import jade.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    /**
     * Just like list of GameObject in the class Scene.
     * This would be looped through when there is any Event happens
     */
    private static List<Observer> observers = new ArrayList<>();

    /**
     * Add an observer to the list
     * @param observer
     */
    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * notify all observers
     * @param obj
     * @param event
     */
    public static void notify(GameObject obj, Event event) {
        for (Observer observer : observers) {
            observer.onNotify(obj, event);
        }
    }
}
