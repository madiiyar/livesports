package livesports.event;

public record PlayerStatsEvent(String playerId, String teamId,
                               int minutesPlayed, int shots) implements Event {
    @Override
    public EventType type() { return EventType.PLAYER_STATS; }
}