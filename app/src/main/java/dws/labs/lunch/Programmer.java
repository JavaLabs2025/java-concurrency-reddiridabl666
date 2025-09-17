package dws.labs.lunch;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Programmer implements Runnable {
    private static final Duration waitTime = Duration.of(10, ChronoUnit.MICROS);

    private final CountDownLatch latch;

    private final List<Spoon> spoons;
    private final List<Boolean> spoonsTaken;

    private final List<Waiter> waiters;

    @Getter
    private int ateSoupPortions = 0;

    @Getter
    private final int id;

    public Programmer(int id, CountDownLatch latch, List<Spoon> spoons, List<Waiter> waiters) {
        this.spoons = spoons;
        this.spoonsTaken = new ArrayList<>(Collections.nCopies(spoons.size(), false));
        this.waiters = waiters;
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {
        while (true) {
            try {
                boolean got = getSpoons();
                if (got) {
                    boolean soupLeft = getAndEatSoup();
                    if (!soupLeft) {
                        break;
                    }
                }

                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                leaveSpoons();
            }
        }

        latch.countDown();
    }

    private boolean getSpoons() throws InterruptedException {
        for (int i = 0; i < spoons.size(); ++i) {
            log.info("[PROGRAMMER {}] Trying to get spoon {}", id, spoons.get(i).getId());

            boolean got = spoons.get(i).take();
            if (!got) {
                return false;
            }

            log.info("[PROGRAMMER {}] Got spoon {}", id, spoons.get(i).getId());
            spoonsTaken.set(i, true);
        }
        return true;
    }

    private void leaveSpoons() {
        for (int i = 0; i < spoons.size(); ++i) {
            if (spoonsTaken.get(i)) {
                spoons.get(i).leave();

                log.info("[PROGRAMMER {}] Left spoon {}", id, spoons.get(i).getId());
                spoonsTaken.set(i, false);
            }
        }
    }

    private boolean getAndEatSoup() throws InterruptedException {
        log.info("[PROGRAMMER {}] Trying to get soup", id);

        // Select waiter with min queue size
        var waiter = waiters.stream().min((first, second) -> first.getQueueSize() - second.getQueueSize());

        waiter.get().addToQueue(this);

        while (true) {
            var result = waiter.get().givePortion(this);
            switch (result) {
                case NoPortionsLeft:
                    log.info("[PROGRAMMER {}] No soup left, ate total: {}", id, ateSoupPortions);
                    return false;
                case Ok:
                    ++ateSoupPortions;
                    log.info("[PROGRAMMER {}] Ate 1 portion of soup, ate total: {}", id, ateSoupPortions);
                    return true;
                case Wait:
                    Thread.sleep(waitTime);
            }
        }
    }
}
