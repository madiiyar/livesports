package livesports.visitor;

import livesports.tree.Team;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamFinderVisitorTest {

    @Test
    void findsTeamDeepInsideTheLeague() {
        Optional<Team> found = SampleTree.LEAGUE.accept(new TeamFinderVisitor("kairat-main"));

        assertEquals("Kairat Main", found.orElseThrow().name());
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertTrue(SampleTree.LEAGUE.accept(new TeamFinderVisitor("nope")).isEmpty());
    }
}