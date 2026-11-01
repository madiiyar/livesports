package livesports.app;

import livesports.tree.Analyst;
import livesports.tree.Club;
import livesports.tree.Coach;
import livesports.tree.League;
import livesports.tree.Physio;
import livesports.tree.Player;
import livesports.tree.Team;
import livesports.visitor.JsonExportVisitor;
import livesports.visitor.TopScorersVisitor;
import livesports.visitor.TotalSalaryVisitor;

import java.util.List;

public final class Main {

    private static final int DEFAULT_THREADS = 3;
    private static final int DEFAULT_EVENTS_PER_PRODUCER = 1000;

    private Main() {}

    public static void main(String[] args) {
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_THREADS;
        int events = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_EVENTS_PER_PRODUCER;

        League league = demoLeague();
        LiveSportsApp app = new LiveSportsApp(league, threads);
        app.playMatch("m1", "astana-main", "p1", events);
        app.reportPlayerStats("p1", "astana-main", 90, 4);
        app.close();

        System.out.println("Threads: " + threads + ", events per producer: " + events);
        System.out.println("Goals by astana-main: " + app.scoreboard().goalsOf("astana-main"));
        System.out.println("Push messages: " + app.push().messages().size());
        System.out.println("Discipline alerts: " + app.disciplineAlerts().alerts().size());
        System.out.println("Total salaries: " + league.accept(new TotalSalaryVisitor()));
        System.out.println("Top scorers: " + league.accept(new TopScorersVisitor(5))
                .stream().map(Player::name).toList());
        System.out.println(league.accept(new JsonExportVisitor()));
    }

    private static League demoLeague() {
        Team astanaMain = new Team("astana-main", "Astana Main", List.of(
                new Player("p1", "Aibek", 1000, 10, 80),
                new Player("p2", "Dias", 800, 4, 70),
                new Coach("c1", "Marat", 500, 10),
                new Physio("f1", "Nurlan", 300),
                new Analyst("a1", "Saule", 200)));
        Team kairatMain = new Team("kairat-main", "Kairat Main", List.of(
                new Player("p4", "Erlan", 900, 7, 75),
                new Coach("c3", "Serik", 500, 8)));
        return new League("kpl", "Premier League", List.of(
                new Club("astana", "Astana FC", List.of(astanaMain)),
                new Club("kairat", "Kairat FC", List.of(kairatMain))));
    }
}