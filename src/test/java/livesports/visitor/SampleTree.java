package livesports.visitor;

import livesports.tree.*;

import java.util.List;

final class SampleTree {

    static final Team ASTANA_MAIN = new Team("astana-main", "Astana Main", List.of(
            new Player("p1", "Aibek", 1000, 10, 80),
            new Player("p2", "Dias", 800, 4, 70),
            new Coach("c1", "Marat", 500, 10),
            new Physio("f1", "Nurlan", 300),
            new Analyst("a1", "Saule", 200)));

    static final Team ASTANA_YOUTH = new Team("astana-youth", "Astana Youth", List.of(
            new Player("p3", "Timur", 200, 2, 60),
            new Coach("c2", "Bolat", 150, 5)));

    static final Team KAIRAT_MAIN = new Team("kairat-main", "Kairat Main", List.of(
            new Player("p4", "Erlan", 900, 7, 75),
            new Coach("c3", "Serik", 500, 8)));

    static final Club ASTANA = new Club("astana", "Astana FC",
            List.of(ASTANA_MAIN, ASTANA_YOUTH));
    static final Club KAIRAT = new Club("kairat", "Kairat FC", List.of(KAIRAT_MAIN));

    static final League LEAGUE = new League("kpl", "Premier League",
            List.of(ASTANA, KAIRAT));

    private SampleTree() {}
}