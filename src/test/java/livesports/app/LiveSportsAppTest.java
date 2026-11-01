package livesports.app;

import livesports.visitor.SampleTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LiveSportsAppTest {

    @Test
    void wholeSystemProducesExpectedCountsWithThreeProducers() {
        LiveSportsApp app = new LiveSportsApp(SampleTree.LEAGUE, 3);

        app.playMatch("m1", "astana-main", "p1", 100);
        app.close();

        // per producer: 100 events = 25 cycles -> 50 goals, 25 fouls, 25 cards
        assertEquals(150, app.scoreboard().goalsOf("astana-main"));   // 3 * 50 goals
        // push gets: 150 goals + 75 cards + 150 squad reports (one per goal)
        assertEquals(375, app.push().messages().size());
        assertFalse(app.disciplineAlerts().alerts().isEmpty());
    }

    @Test
    void secondSourceFeedsTheDashboard() {
        LiveSportsApp app = new LiveSportsApp(SampleTree.LEAGUE, 3);

        app.reportPlayerStats("p1", "astana-main", 90, 4);
        app.close();

        assertEquals(4, app.dashboard().latestOf("p1").orElseThrow().shots());
    }
}