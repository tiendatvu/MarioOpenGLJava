package observers.events;

public class Event {
    public EventType type;

    /**
     * Init with the
     * @param type
     */
    public Event(EventType type) {
        this.type = type;
    }

    /**
     * TODO: why init with userevent
     */
    public Event() {
        this.type = EventType.UserEvent;
    }
}
