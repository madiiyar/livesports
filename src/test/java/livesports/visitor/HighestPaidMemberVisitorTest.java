package livesports.visitor;

import livesports.tree.Member;
import livesports.tree.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighestPaidMemberVisitorTest {

    @Test
    void findsHighestPaidInWholeLeague() {
        Optional<Member> best = SampleTree.LEAGUE.accept(new HighestPaidMemberVisitor());

        assertEquals("p1", best.orElseThrow().id());      // 1000 is the maximum
    }

    @Test
    void findsHighestPaidInOneClubSubtree() {
        Optional<Member> best = SampleTree.KAIRAT.accept(new HighestPaidMemberVisitor());

        assertEquals("p4", best.orElseThrow().id());      // 900 beats the coach's 500
    }

    @Test
    void emptyTeamHasNoHighestPaid() {
        Team empty = new Team("e1", "Empty", List.of());

        assertTrue(empty.accept(new HighestPaidMemberVisitor()).isEmpty());
    }
}