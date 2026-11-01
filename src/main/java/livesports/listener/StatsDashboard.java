package livesports.listener;

import livesports.event.PlayerStatsEvent;
import livesports.observer.Observer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class StatsDashboard implements Observer<PlayerStatsEvent> {

    private final Map<String, PlayerStatsEvent> latestByPlayer = new ConcurrentHashMap<>();

    @Override
    public void onEvent(PlayerStatsEvent event) {
        latestByPlayer.put(event.playerId(), event);
    }

    public Optional<PlayerStatsEvent> latestOf(String playerId) {
        return Optional.ofNullable(latestByPlayer.get(playerId));
    }
}