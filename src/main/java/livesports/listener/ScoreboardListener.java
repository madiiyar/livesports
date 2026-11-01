package livesports.listener;

import livesports.event.MatchEvent;
import livesports.observer.Observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ScoreboardListener implements Observer<MatchEvent> {

    private final Map<String, AtomicInteger> goalsByTeam = new ConcurrentHashMap<>();

    @Override
    public void onEvent(MatchEvent event) {
        goalsByTeam.computeIfAbsent(event.teamId(), t -> new AtomicInteger())
                .incrementAndGet();
    }

    public int goalsOf(String teamId) {
        AtomicInteger goals = goalsByTeam.get(teamId);
        return goals == null ? 0 : goals.get();
    }
}