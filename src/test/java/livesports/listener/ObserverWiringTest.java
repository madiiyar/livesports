package livesports.listener;

import livesports.event.EventType;
import livesports.source.MatchEventSource;
import livesports.source.PlayerStatsSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObserverWiringTest {

    @Test
    void scoreboardCountsOnlyGoals() {
        MatchEventSource source = new MatchEventSource();
        ScoreboardListener scoreboard = new ScoreboardListener();
        source.subscribe(EventType.GOAL, scoreboard);

        source.goal("m1", "t1", "p1", 10);
        source.goal("m1", "t1", "p2", 55);
        source.foul("m1", "t1", "p3", 60);

        assertEquals(2, scoreboard.goalsOf("t1"));
        assertEquals(0, scoreboard.goalsOf("t2"));
    }

    @Test
    void disciplineChainReachesAlertListener() {
        MatchEventSource source = new MatchEventSource();
        TeamDisciplineAggregator aggregator = new TeamDisciplineAggregator();
        DisciplineAlertListener alerts = new DisciplineAlertListener(3);
        source.subscribe(EventType.FOUL, aggregator);
        source.subscribe(EventType.CARD, aggregator);
        aggregator.subscribe(EventType.TEAM_DISCIPLINE, alerts);

        source.foul("m1", "t1", "p1", 5);    // total 1: below threshold
        assertEquals(0, alerts.alerts().size());

        source.card("m1", "t1", "p1", 30);   // total 1 + 3 = 4: alert
        assertEquals(1, alerts.alerts().size());
    }

    @Test
    void pushNotifierListensToBothSources() {
        MatchEventSource matches = new MatchEventSource();
        PlayerStatsSource stats = new PlayerStatsSource();
        PushNotifier push = new PushNotifier();
        matches.subscribe(EventType.GOAL, push);
        stats.subscribe(EventType.PLAYER_STATS, push);

        matches.goal("m1", "t1", "p1", 10);
        stats.report("p1", "t1", 90, 4);

        assertEquals(2, push.messages().size());
    }

    @Test
    void dashboardKeepsLatestStatsPerPlayer() {
        PlayerStatsSource stats = new PlayerStatsSource();
        StatsDashboard dashboard = new StatsDashboard();
        stats.subscribe(EventType.PLAYER_STATS, dashboard);

        stats.report("p1", "t1", 45, 2);
        stats.report("p1", "t1", 90, 5);

        assertEquals(90, dashboard.latestOf("p1").orElseThrow().minutesPlayed());
    }
}