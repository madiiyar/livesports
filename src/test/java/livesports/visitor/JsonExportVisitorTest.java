package livesports.visitor;

import livesports.tree.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonExportVisitorTest {

    // Single quotes in the expected text are swapped to double quotes,
    // so the expected JSON stays readable.
    private static String json(String withSingleQuotes) {
        return withSingleQuotes.replace('\'', '"');
    }

    @Test
    void exportsTeamWithMembersArray() {
        String expected = json("{'type':'team','id':'astana-youth','name':'Astana Youth','members':["
                + "{'type':'player','id':'p3','name':'Timur','salary':200,'goals':2,'rating':60},"
                + "{'type':'coach','id':'c2','name':'Bolat','salary':150,'experienceYears':5}]}");

        assertEquals(expected, SampleTree.ASTANA_YOUTH.accept(new JsonExportVisitor()));
    }

    @Test
    void nestsClubTeamAndMembersCorrectly() {
        String expected = json("{'type':'club','id':'kairat','name':'Kairat FC','teams':["
                + "{'type':'team','id':'kairat-main','name':'Kairat Main','members':["
                + "{'type':'player','id':'p4','name':'Erlan','salary':900,'goals':7,'rating':75},"
                + "{'type':'coach','id':'c3','name':'Serik','salary':500,'experienceYears':8}]}]}");

        assertEquals(expected, SampleTree.KAIRAT.accept(new JsonExportVisitor()));
    }

    @Test
    void escapesQuotesInNames() {
        Player tricky = new Player("x1", "Ali \"Ace\"", 1, 0, 0);

        String result = tricky.accept(new JsonExportVisitor());

        assertTrue(result.contains("\"name\":\"Ali \\\"Ace\\\"\""));
    }
}