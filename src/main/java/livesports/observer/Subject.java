package livesports.observer;

import livesports.event.Event;
import livesports.event.EventType;

public interface Subject<E extends Event> {
    void subscribe(EventType type, Observer<? super E> observer);
    void unsubscribe(EventType type, Observer<? super E> observer);
}