package livesports.simulation;

import livesports.source.MatchEventSource;

public final class MatchProducer implements Runnable {

    private final MatchEventSource source;
    private final String matchId;
    private final String teamId;
    private final String playerId;
    private final int eventCount;

    public MatchProducer(MatchEventSource source, String matchId, String teamId,
                         String playerId, int eventCount) {
        if (eventCount < 0) throw new IllegalArgumentException("eventCount must be >= 0");
        this.source = source;
        this.matchId = matchId;
        this.teamId = teamId;
        this.playerId = playerId;
        this.eventCount = eventCount;
    }

    /** Every 4 events: goal, goal, foul, card, so expected counts are easy to calculate. */
    @Override
    public void run() {
        for (int i = 0; i < eventCount; i++) {
            int minute = i % 90;
            switch (i % 4) {
                case 0, 1 -> source.goal(matchId, teamId, playerId, minute);
                case 2 -> source.foul(matchId, teamId, playerId, minute);
                default -> source.card(matchId, teamId, playerId, minute);
            }
        }
    }
}