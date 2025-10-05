package dws.labs.lunch;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Waiter implements Runnable {
    private static final Duration waitTime = Duration.of(10, ChronoUnit.MICROS);

    private final int id;

    private final Soup soup;

    private final WaiterBell bell;

    private final CountDownLatch latch;

    public Waiter(int id, CountDownLatch latch, WaiterBell bell, Soup soup) {
        this.id = id;
        this.latch = latch;
        this.bell = bell;
        this.soup = soup;
    }

    @Override
    public void run() {
        try {
            while (!bell.haveAllProgrammersFinished()) {
                var programmer = bell.poll();
                if (programmer == null) {
                    Thread.sleep(waitTime);
                    continue;
                }

                log.info("[WAITER {}] Getting portion for programmer {}", id, programmer.getId());

                while (true) {
                    if (soup.getPortion()) {
                        programmer.addFood();
                        bell.ring(programmer); // Add programmer back to the queue - so that all programmers get equal amount
                                               // of food
                        break;
                    }

                    if (soup.getPortionsLeft() <= 0) {
                        programmer.sayThatFoodIsGone();
                        break;
                    }
                }

                Thread.sleep(waitTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            log.info("[WAITER {}] No soup left", id);

            latch.countDown();
        }
    }
}
