package livesports.visitor;

import livesports.tree.Player;
import livesports.tree.Scout;
import livesports.tree.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoutSupportTest {

    private static final Team SCOUTED = new Team("t-scout", "Scouted Team", List.of(
            new Player("p9", "Ruslan", 600, 3, 65),
            new Scout("s1", "Gulnar", 400, "Europe")));

    @Test
    void scoutSalaryIsIncludedInTotal() {
        assertEquals(1000, SCOUTED.accept(new TotalSalaryVisitor()));   // 600 + 400
    }

    @Test
    void scoutCountsAsStaff() {
        RosterStatsVisitor stats = new RosterStatsVisitor();

        SCOUTED.accept(stats);

        assertEquals(1, stats.playerCount());
        assertEquals(1, stats.staffCount());
    }

    @Test
    void scoutIsExportedAsJson() {
        String json = new Scout("s1", "Gulnar", 400, "Europe").accept(new JsonExportVisitor());

        assertEquals("{\"type\":\"scout\",\"id\":\"s1\",\"name\":\"Gulnar\","
                + "\"salary\":400,\"region\":\"Europe\"}", json);
    }
}