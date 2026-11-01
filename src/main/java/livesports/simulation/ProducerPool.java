package livesports.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ProducerPool {

    private static final long TIMEOUT_SECONDS = 30;

    private final ExecutorService executor;

    public ProducerPool(ExecutorService executor) {
        this.executor = executor;
    }

    /** Runs all producers in parallel and returns only when every one has finished. */
    public void runAll(List<? extends Runnable> producers) {
        List<Future<?>> futures = new ArrayList<>();
        for (Runnable producer : producers) {
            futures.add(executor.submit(producer));
        }
        for (Future<?> future : futures) {
            awaitCompletion(future);
        }
    }

    /** Stops accepting tasks, lets running ones finish, waits until all threads are gone. */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                throw new IllegalStateException("Producer threads did not stop in time");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while shutting down", e);
        }
    }

    private void awaitCompletion(Future<?> future) {
        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for producers", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("A producer failed", e.getCause());
        } catch (TimeoutException e) {
            throw new IllegalStateException("A producer timed out", e);
        }
    }
}