package livesports.observer;

import livesports.event.Event;

@FunctionalInterface
public interface Observer<E extends Event> {
    void onEvent(E event);
}