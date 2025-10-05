package dws.labs.lunch;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Waiter implements Runnable {
    private static final Duration waitTime = Duration.of(10, ChronoUnit.MICROS);

    private final List<Programmer> programmers;

    private final int id;

    private final Soup soup;

    private final CountDownLatch latch;

    public Waiter(int id, CountDownLatch latch, Soup soup, List<Programmer> programmers) {
        this.id = id;
        this.latch = latch;
        this.soup = soup;
        this.programmers = programmers;
    }

    @Override
    public void run() {
        try {
            while (soup.getPortionsLeft() > 0) {
                for (var programmer : programmers) {
                    if (!programmer.isReadyToEat()) {
                        continue;
                    }

                    boolean ok = programmer.prepareForFood();
                    if (!ok) {
                        continue;
                    }

                    log.info("[WAITER {}] Getting portion for programmer {}", id, programmer.getId());

                    while (true) {
                        if (soup.getPortion()) {
                            break;
                        }

                        if (soup.getPortionsLeft() <= 0) {
                            return;
                        }
                    }

                    programmer.addFood();
                }

                Thread.sleep(waitTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            log.info("[WAITER {}] No soup left", id);

            for (var programmer : programmers) {
                programmer.sayThatSoupIsGone();
            }
            latch.countDown();
        }
    }
}
