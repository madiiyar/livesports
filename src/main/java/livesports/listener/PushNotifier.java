package livesports.listener;

import livesports.event.Event;
import livesports.observer.Observer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class PushNotifier implements Observer<Event> {

    private final Queue<String> messages = new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(Event event) {
        messages.add(event.type() + ": " + event);
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}