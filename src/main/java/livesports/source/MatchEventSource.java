package livesports.source;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.observer.ListenerRegistry;
import livesports.observer.Observer;
import livesports.observer.Subject;

public final class MatchEventSource implements Subject<MatchEvent> {

    private final ListenerRegistry<MatchEvent> registry = new ListenerRegistry<>();

    @Override
    public void subscribe(EventType type, Observer<? super MatchEvent> observer) {
        registry.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super MatchEvent> observer) {
        registry.unsubscribe(type, observer);
    }

    public void publish(MatchEvent event) {
        registry.publish(event);
    }
}