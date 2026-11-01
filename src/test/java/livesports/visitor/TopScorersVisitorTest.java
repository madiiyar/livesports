package livesports.visitor;

import livesports.tree.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopScorersVisitorTest {

    @Test
    void returnsPlayersWithAtLeastMinGoalsInTreeOrder() {
        List<Player> result = SampleTree.LEAGUE.accept(new TopScorersVisitor(5));

        // p1 has 10 goals, p4 has 7 goals; p2 (4) and p3 (2) are below 5
        assertEquals(List.of("p1", "p4"), result.stream().map(Player::id).toList());
    }

    @Test
    void returnsEmptyListWhenNobodyQualifies() {
        List<Player> result = SampleTree.ASTANA_YOUTH.accept(new TopScorersVisitor(100));

        assertTrue(result.isEmpty());
    }
}