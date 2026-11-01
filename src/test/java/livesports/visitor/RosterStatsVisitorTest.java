package livesports.visitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RosterStatsVisitorTest {

    @Test
    void accumulatesAcrossWholeLeague() {
        RosterStatsVisitor stats = new RosterStatsVisitor();

        SampleTree.LEAGUE.accept(stats);

        assertEquals(4, stats.playerCount());          // p1, p2, p3, p4
        assertEquals(5, stats.staffCount());           // 3 coaches + 1 physio + 1 analyst
        assertEquals(23, stats.totalGoals());          // 10 + 4 + 2 + 7
        assertEquals(71.25, stats.averageRating(), 0.001); // (80+70+60+75) / 4
    }

    @Test
    void accumulatesOnlyInsideOneTeam() {
        RosterStatsVisitor stats = new RosterStatsVisitor();

        SampleTree.ASTANA_MAIN.accept(stats);

        assertEquals(2, stats.playerCount());
        assertEquals(3, stats.staffCount());           // coach + physio + analyst
        assertEquals(14, stats.totalGoals());          // 10 + 4
        assertEquals(75.0, stats.averageRating(), 0.001); // (80+70) / 2
    }
}