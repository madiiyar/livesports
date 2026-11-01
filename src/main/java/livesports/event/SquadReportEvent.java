package livesports.event;

public record SquadReportEvent(String teamId, int players, int seasonGoals,
                               double averageRating) implements Event {
    @Override
    public EventType type() { return EventType.SQUAD_REPORT; }
}