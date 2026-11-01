package livesports.listener;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.event.SquadReportEvent;
import livesports.visitor.SampleTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SquadReportListenerTest {

    private final SquadReportListener listener = new SquadReportListener(SampleTree.LEAGUE);
    private final List<SquadReportEvent> reports = new ArrayList<>();

    SquadReportListenerTest() {
        listener.subscribe(EventType.SQUAD_REPORT, reports::add);
    }

    private static MatchEvent goalFor(String teamId) {
        return new MatchEvent(EventType.GOAL, "m1", teamId, "p3", 12);
    }

    @Test
    void reportCoversOnlyTheTeamThatScored() {
        listener.onEvent(goalFor("astana-youth"));

        SquadReportEvent report = reports.get(0);
        assertEquals("astana-youth", report.teamId());
        assertEquals(1, report.players());          // only p3, not the 4 players of the league
        assertEquals(2, report.seasonGoals());
        assertEquals(60.0, report.averageRating(), 0.001);
    }

    @Test
    void reportForBiggerTeamUsesHandCalculatedValues() {
        listener.onEvent(goalFor("astana-main"));

        SquadReportEvent report = reports.get(0);
        assertEquals(2, report.players());          // p1, p2
        assertEquals(14, report.seasonGoals());     // 10 + 4
        assertEquals(75.0, report.averageRating(), 0.001); // (80 + 70) / 2
    }

    @Test
    void unknownTeamProducesNoReport() {
        listener.onEvent(goalFor("no-such-team"));

        assertTrue(reports.isEmpty());
    }
}