package service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ✅ CS622 Part 2.3 – New class introduced to support periodic autosave
 * Periodically saves user registry data (patients only) to binary files.
 */
public class RegistryAutosaveService {

    private final UserRegistry registry;
    private final ScheduledExecutorService scheduler;
    private final long intervalSeconds;

    public RegistryAutosaveService(UserRegistry registry, long intervalSeconds) {
        this.registry = registry;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (registry.isDirty()) {
                    registry.saveAllUsersToBinaryFile();
                    registry.resetDirtyFlag();
                    // silent success
                }
                // else: skip silently
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    if (registry.isDirty()) {
                        registry.saveAllUsersToBinaryFile();
                        registry.resetDirtyFlag();
                    }
                } catch (Exception ignored) {
                    // fail silently
                }
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
