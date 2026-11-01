package livesports.source;

import livesports.event.PlayerStatsEvent;
import livesports.observer.AbstractSubject;

public final class PlayerStatsSource extends AbstractSubject<PlayerStatsEvent> {

    public void report(String playerId, String teamId, int minutesPlayed, int shots) {
        publish(new PlayerStatsEvent(playerId, teamId, minutesPlayed, shots));
    }
}