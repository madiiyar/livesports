package livesports.event;

public record TeamDisciplineEvent(String teamId, int totalPoints) implements Event {
    @Override
    public EventType type() { return EventType.TEAM_DISCIPLINE; }
}