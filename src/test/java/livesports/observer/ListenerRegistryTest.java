package livesports.observer;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListenerRegistryTest {

    private final ListenerRegistry<MatchEvent> registry = new ListenerRegistry<>();

    private static MatchEvent goal() {
        return new MatchEvent(EventType.GOAL, "m1", "t1", "p1", 10);
    }

    @Test
    void deliversOnlyToListenersOfThatType() {
        AtomicInteger goals = new AtomicInteger();
        AtomicInteger fouls = new AtomicInteger();
        registry.subscribe(EventType.GOAL, e -> goals.incrementAndGet());
        registry.subscribe(EventType.FOUL, e -> fouls.incrementAndGet());

        registry.publish(goal());

        assertEquals(1, goals.get());
        assertEquals(0, fouls.get());
    }

    @Test
    void listenerCanUnsubscribeItselfInsideHandler() {
        AtomicInteger calls = new AtomicInteger();
        Observer<MatchEvent> oneShot = new Observer<>() {
            @Override
            public void onEvent(MatchEvent event) {
                calls.incrementAndGet();
                registry.unsubscribe(EventType.GOAL, this);
            }
        };
        registry.subscribe(EventType.GOAL, oneShot);

        registry.publish(goal());
        registry.publish(goal());

        assertEquals(1, calls.get());
    }
}