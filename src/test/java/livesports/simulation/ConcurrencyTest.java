package livesports.simulation;

import livesports.event.EventType;
import livesports.event.MatchEvent;
import livesports.listener.ScoreboardListener;
import livesports.listener.TeamDisciplineAggregator;
import livesports.observer.Observer;
import livesports.source.MatchEventSource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrencyTest {

    private static final int THREADS = 4;
    private static final int EVENTS_PER_PRODUCER = 1000;

    private static List<MatchProducer> producers(MatchEventSource source) {
        List<MatchProducer> list = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            list.add(new MatchProducer(source, "m1", "t1", "p" + i, EVENTS_PER_PRODUCER));
        }
        return list;
    }

    private static void runAndShutdown(List<MatchProducer> producers) {
        ProducerPool pool = new ProducerPool(Executors.newFixedThreadPool(THREADS));
        pool.runAll(producers);
        pool.shutdown();
    }

    @Test
    void stressTestFourThreadsThousandEventsEach() {
        MatchEventSource source = new MatchEventSource();
        ScoreboardListener scoreboard = new ScoreboardListener();
        TeamDisciplineAggregator discipline = new TeamDisciplineAggregator();
        AtomicInteger disciplineEvents = new AtomicInteger();
        AtomicInteger maxPoints = new AtomicInteger();
        source.subscribe(EventType.GOAL, scoreboard);
        source.subscribe(EventType.FOUL, discipline);
        source.subscribe(EventType.CARD, discipline);
        discipline.subscribe(EventType.TEAM_DISCIPLINE, e -> {
            disciplineEvents.incrementAndGet();
            maxPoints.accumulateAndGet(e.totalPoints(), Math::max);
        });

        runAndShutdown(producers(source));

        // per producer: 1000 events = 250 cycles of (goal, goal, foul, card)
        assertEquals(2000, scoreboard.goalsOf("t1"));   // 4 producers * 500 goals
        assertEquals(2000, disciplineEvents.get());     // 4 * (250 fouls + 250 cards)
        assertEquals(4000, maxPoints.get());            // 4 * (250 * 1 + 250 * 3)
    }

    @Test
    void unsubscribingWhilePublishingNeverLosesEventsForOtherListeners() {
        MatchEventSource source = new MatchEventSource();
        AtomicInteger stableGoals = new AtomicInteger();
        source.subscribe(EventType.GOAL, e -> stableGoals.incrementAndGet());
        List<SelfRemovingListener> transients = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            SelfRemovingListener listener = new SelfRemovingListener(source);
            transients.add(listener);
            source.subscribe(EventType.GOAL, listener);
        }

        runAndShutdown(producers(source));

        assertEquals(2000, stableGoals.get());
        for (SelfRemovingListener listener : transients) {
            assertTrue(listener.calls() >= 1);
        }
    }

    private static final class SelfRemovingListener implements Observer<MatchEvent> {
        private final MatchEventSource source;
        private final AtomicInteger calls = new AtomicInteger();

        SelfRemovingListener(MatchEventSource source) {
            this.source = source;
        }

        @Override
        public void onEvent(MatchEvent event) {
            calls.incrementAndGet();
            source.unsubscribe(EventType.GOAL, this);
        }

        int calls() {
            return calls.get();
        }
    }
}