package livesports.listener;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.event.TeamDisciplineEvent;
import livesports.observer.AbstractSubject;
import livesports.observer.Observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class TeamDisciplineAggregator extends AbstractSubject<TeamDisciplineEvent>
        implements Observer<MatchEvent> {

    private static final int FOUL_POINTS = 1;
    private static final int CARD_POINTS = 3;

    private final Map<String, AtomicInteger> pointsByTeam = new ConcurrentHashMap<>();

    @Override
    public void onEvent(MatchEvent event) {
        int points = event.type() == EventType.CARD ? CARD_POINTS : FOUL_POINTS;
        int total = pointsByTeam.computeIfAbsent(event.teamId(), t -> new AtomicInteger())
                .addAndGet(points);
        publish(new TeamDisciplineEvent(event.teamId(), total));
    }
}