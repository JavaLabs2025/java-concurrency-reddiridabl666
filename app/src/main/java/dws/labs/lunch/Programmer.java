package dws.labs.lunch;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Programmer implements Runnable {
    private static final Duration waitAfterBiteTime = Duration.of(10, ChronoUnit.MICROS);
    private static final int waitAfterAtePortionTimeMicros = 100;

    private static final int BOWL_SIZE = 100;

    private static final int MAX_BITE_SIZE = 50;

    private final CountDownLatch latch;

    private final List<Spoon> spoons;
    private final List<Boolean> spoonsTaken;

    private final WaiterBell bell;

    private final AtomicBoolean hasFood = new AtomicBoolean(false);
    private final AtomicBoolean soupGone = new AtomicBoolean(false);

    private int soupLeftInBowl = 0;

    @Getter
    private int ateSoupPortions = 0;

    @Getter
    private final int id;

    public Programmer(int id, CountDownLatch latch, WaiterBell bell, List<Spoon> spoons) {
        this.spoons = spoons;
        this.spoonsTaken = new ArrayList<>(Collections.nCopies(spoons.size(), false));
        this.id = id;
        this.latch = latch;
        this.bell = bell;
    }

    public void sayThatFoodIsGone() {
        soupGone.set(true);
    }

    public void addFood() {
        soupLeftInBowl = BOWL_SIZE;
        hasFood.set(true);
    }

    @Override
    public void run() {
        while (soupGone.compareAndSet(false, false)) {
            try {
                boolean soupLeft = getSoup();
                if (!soupLeft) {
                    break;
                }

                getSpoons();

                eatSoup();

                Thread.sleep(Duration.of(100 + (long) (Math.random() * waitAfterAtePortionTimeMicros), ChronoUnit.MICROS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                leaveSpoons();
            }
        }

        log.info("[PROGRAMMER {}] No soup left {}", id);
        bell.programmerFinised(id);

        latch.countDown();
    }

    private void getSpoons() throws InterruptedException {
        for (int i = 0; i < spoons.size(); ++i) {
            log.info("[PROGRAMMER {}] Trying to get spoon {}", id, spoons.get(i).getId());

            spoons.get(i).take();
            spoonsTaken.set(i, true);

            log.info("[PROGRAMMER {}] Got spoon {}", id, spoons.get(i).getId());
        }
    }

    private void leaveSpoons() {
        for (int i = 0; i < spoons.size(); ++i) {
            if (!spoonsTaken.get(i)) {
                continue;
            }

            spoons.get(i).leave();
            spoonsTaken.set(i, false);

            log.info("[PROGRAMMER {}] Left spoon {}", id, spoons.get(i).getId());
        }
    }

    private void eatSoup() throws InterruptedException {
        while (soupLeftInBowl > 0) {
            int amount = (int) (Math.random() * MAX_BITE_SIZE) + 1;
            if (amount > soupLeftInBowl) {
                amount = soupLeftInBowl;
            }

            soupLeftInBowl -= amount;

            log.info("[PROGRAMMER {}] Ate {}% of soup in his bowl, left in bowl: {}%", id, amount, soupLeftInBowl);

            Thread.sleep(waitAfterBiteTime);
        }

        ++ateSoupPortions;
        hasFood.set(false);

        log.info("[PROGRAMMER {}] Ate 1 portion of soup, ate total: {}", id, ateSoupPortions);
    }

    private boolean getSoup() {
        log.info("[PROGRAMMER {}] Trying to get soup", id);

        bell.ring(this);

        while (!hasFood.compareAndSet(true, true)) {
            if (soupGone.compareAndSet(true, true)) {
                return false;
            }
        }

        return true;
    }
}
