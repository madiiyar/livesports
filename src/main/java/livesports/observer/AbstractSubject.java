package livesports.observer;

import livesports.event.Event;
import livesports.event.EventType;

public abstract class AbstractSubject<E extends Event> implements Subject<E> {

    private final ListenerRegistry<E> registry = new ListenerRegistry<>();

    @Override
    public final void subscribe(EventType type, Observer<? super E> observer) {
        registry.subscribe(type, observer);
    }

    @Override
    public final void unsubscribe(EventType type, Observer<? super E> observer) {
        registry.unsubscribe(type, observer);
    }

    protected final void publish(E event) {
        registry.publish(event);
    }
}