package livesports.source;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.observer.AbstractSubject;

public final class MatchEventSource extends AbstractSubject<MatchEvent> {

    public void goal(String matchId, String teamId, String playerId, int minute) {
        publish(new MatchEvent(EventType.GOAL, matchId, teamId, playerId, minute));
    }

    public void foul(String matchId, String teamId, String playerId, int minute) {
        publish(new MatchEvent(EventType.FOUL, matchId, teamId, playerId, minute));
    }

    public void card(String matchId, String teamId, String playerId, int minute) {
        publish(new MatchEvent(EventType.CARD, matchId, teamId, playerId, minute));
    }
}