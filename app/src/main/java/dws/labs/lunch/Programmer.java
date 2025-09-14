package dws.labs.lunch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Programmer implements Runnable {
    private static final Duration waitTime = Duration.ofMillis(10);

    private final CountDownLatch latch;

    private final List<Spoon> spoons;
    private final List<Boolean> spoonsTaken;

    private final Soup soup;

    private final int id;

    private int needSoupPortions;

    public Programmer(int id, CountDownLatch latch, List<Spoon> spoons, Soup soup, int needSoup) {
        this.spoons = spoons;
        this.spoonsTaken = new ArrayList<>(Collections.nCopies(spoons.size(), false));
        this.soup = soup;
        this.needSoupPortions = needSoup;
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {
        while (needSoupPortions > 0) {
            try {
                boolean got = getSpoons();
                if (got) {
                    eatSoup();
                }

                leaveSpoons();

                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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

    private void eatSoup() {
        log.info("[PROGRAMMER {}] Trying to get soup", id);

        soup.getPortion();

        needSoupPortions -= 1;

        log.info("[PROGRAMMER {}] Ate 1 portion of soup, {} left", id, needSoupPortions);
    }
}
