package livesports.event;

import java.util.Objects;

public record MatchEvent(EventType type, String matchId, String teamId,
                         String playerId, int minute) implements Event {
    public MatchEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(matchId);
        Objects.requireNonNull(teamId);
        Objects.requireNonNull(playerId);
        if (minute < 0) throw new IllegalArgumentException("minute must be >= 0");
    }
}