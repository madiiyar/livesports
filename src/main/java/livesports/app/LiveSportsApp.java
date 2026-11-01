package livesports.app;

import livesports.event.EventType;
import livesports.listener.DisciplineAlertListener;
import livesports.listener.PushNotifier;
import livesports.listener.ScoreboardListener;
import livesports.listener.SquadReportListener;
import livesports.listener.StatsDashboard;
import livesports.listener.TeamDisciplineAggregator;
import livesports.simulation.MatchProducer;
import livesports.simulation.ProducerPool;
import livesports.source.MatchEventSource;
import livesports.source.PlayerStatsSource;
import livesports.tree.League;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/** The composition root: the only place where objects are created and wired together. */
public final class LiveSportsApp implements AutoCloseable {

    private static final int DISCIPLINE_ALERT_THRESHOLD = 10;

    private final int producerThreads;
    private final MatchEventSource matchSource;
    private final PlayerStatsSource statsSource;
    private final ScoreboardListener scoreboard;
    private final TeamDisciplineAggregator discipline;
    private final DisciplineAlertListener disciplineAlerts;
    private final SquadReportListener squadReports;
    private final PushNotifier push;
    private final StatsDashboard dashboard;
    private final ProducerPool producers;

    public LiveSportsApp(League league, int producerThreads) {
        if (producerThreads < 1) {
            throw new IllegalArgumentException("producerThreads must be >= 1");
        }
        this.producerThreads = producerThreads;
        this.matchSource = new MatchEventSource();
        this.statsSource = new PlayerStatsSource();
        this.scoreboard = new ScoreboardListener();
        this.discipline = new TeamDisciplineAggregator();
        this.disciplineAlerts = new DisciplineAlertListener(DISCIPLINE_ALERT_THRESHOLD);
        this.squadReports = new SquadReportListener(league);
        this.push = new PushNotifier();
        this.dashboard = new StatsDashboard();
        this.producers = new ProducerPool(Executors.newFixedThreadPool(producerThreads));
        subscribeListeners();
    }

    private void subscribeListeners() {
        matchSource.subscribe(EventType.GOAL, scoreboard);
        matchSource.subscribe(EventType.GOAL, push);
        matchSource.subscribe(EventType.CARD, push);
        matchSource.subscribe(EventType.GOAL, squadReports);
        matchSource.subscribe(EventType.FOUL, discipline);
        matchSource.subscribe(EventType.CARD, discipline);
        discipline.subscribe(EventType.TEAM_DISCIPLINE, disciplineAlerts);
        squadReports.subscribe(EventType.SQUAD_REPORT, push);
        statsSource.subscribe(EventType.PLAYER_STATS, dashboard);
    }

    public void playMatch(String matchId, String teamId, String playerId,
                          int eventsPerProducer) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < producerThreads; i++) {
            tasks.add(new MatchProducer(matchSource, matchId, teamId, playerId,
                    eventsPerProducer));
        }
        producers.runAll(tasks);
    }

    public void reportPlayerStats(String playerId, String teamId, int minutes, int shots) {
        statsSource.report(playerId, teamId, minutes, shots);
    }

    public ScoreboardListener scoreboard() { return scoreboard; }
    public PushNotifier push() { return push; }
    public DisciplineAlertListener disciplineAlerts() { return disciplineAlerts; }
    public StatsDashboard dashboard() { return dashboard; }

    @Override
    public void close() {
        producers.shutdown();
    }
}