package livesports.simulation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProducerPoolTest {

    @Test
    void shutdownLeavesNoRunningThreads() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ProducerPool pool = new ProducerPool(executor);
        AtomicInteger done = new AtomicInteger();
        List<Runnable> tasks = List.of(done::incrementAndGet, done::incrementAndGet,
                done::incrementAndGet);

        pool.runAll(tasks);
        pool.shutdown();

        assertEquals(3, done.get());
        assertTrue(executor.isTerminated());
    }

    @Test
    void producerFailureIsReportedNotSwallowed() {
        ProducerPool pool = new ProducerPool(Executors.newFixedThreadPool(1));
        Runnable failing = () -> { throw new IllegalStateException("boom"); };

        assertThrows(IllegalStateException.class, () -> pool.runAll(List.of(failing)));

        pool.shutdown();
    }
}