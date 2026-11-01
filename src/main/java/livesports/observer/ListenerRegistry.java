package livesports.observer;

import livesports.event.Event;
import livesports.event.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ListenerRegistry<E extends Event> {

    private final Map<EventType, CopyOnWriteArrayList<Observer<? super E>>> byType =
            new ConcurrentHashMap<>();

    public void subscribe(EventType type, Observer<? super E> observer) {
        byType.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public void unsubscribe(EventType type, Observer<? super E> observer) {
        CopyOnWriteArrayList<Observer<? super E>> list = byType.get(type);
        if (list != null) list.remove(observer);
    }

    public void publish(E event) {
        CopyOnWriteArrayList<Observer<? super E>> list = byType.get(event.type());
        if (list == null) return;
        for (Observer<? super E> observer : list) {
            observer.onEvent(event);
        }
    }
}