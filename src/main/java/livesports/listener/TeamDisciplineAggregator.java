package livesports.listener;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.event.TeamDisciplineEvent;
import livesports.observer.ListenerRegistry;
import livesports.observer.Observer;
import livesports.observer.Subject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class TeamDisciplineAggregator
        implements Observer<MatchEvent>, Subject<TeamDisciplineEvent> {

    private static final int FOUL_POINTS = 1;
    private static final int CARD_POINTS = 3;

    private final ListenerRegistry<TeamDisciplineEvent> registry = new ListenerRegistry<>();
    private final Map<String, AtomicInteger> pointsByTeam = new ConcurrentHashMap<>();

    @Override
    public void onEvent(MatchEvent event) {
        int points = event.type() == EventType.CARD ? CARD_POINTS : FOUL_POINTS;
        int total = pointsByTeam.computeIfAbsent(event.teamId(), t -> new AtomicInteger())
                .addAndGet(points);
        registry.publish(new TeamDisciplineEvent(event.teamId(), total));
    }

    @Override
    public void subscribe(EventType type, Observer<? super TeamDisciplineEvent> observer) {
        registry.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super TeamDisciplineEvent> observer) {
        registry.unsubscribe(type, observer);
    }
}