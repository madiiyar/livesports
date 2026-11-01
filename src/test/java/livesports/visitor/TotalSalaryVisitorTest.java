package livesports.visitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalSalaryVisitorTest {

    @Test
    void sumsWholeLeague() {
        // (1000+800+500+300+200) + (200+150) + (900+500) = 2800 + 350 + 1400
        assertEquals(4550, SampleTree.LEAGUE.accept(new TotalSalaryVisitor()));
    }

    @Test
    void sumsOnlyOneClubSubtree() {
        assertEquals(3150, SampleTree.ASTANA.accept(new TotalSalaryVisitor()));
    }

    @Test
    void sumsSingleTeam() {
        assertEquals(350, SampleTree.ASTANA_YOUTH.accept(new TotalSalaryVisitor()));
    }
}