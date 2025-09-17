package dws.labs.lunch;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Waiter {
    public enum SoupState {
        Wait,
        Ok,
        NoPortionsLeft
    }

    private final BlockingQueue<Programmer> queue;

    private final int id;

    private final Soup soup;

    public Waiter(int id, Soup soup, int programmersNum) {
        this.id = id;
        this.queue = new ArrayBlockingQueue<>(programmersNum);
        this.soup = soup;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void addToQueue(Programmer programmer) {
        log.info("[WAITER {}] Add programmer {} to queue", id, programmer.getId());
        queue.add(programmer);
    }

    public SoupState givePortion(Programmer programmer) {
        if (queue.peek() == programmer) {
            log.info("[WAITER {}] Getting portion for programmer {}", id, programmer.getId());

            while (true) {
                if (soup.getPortion()) {
                    break;
                }

                if (soup.getPortionsLeft() <= 0) {
                    log.info("[WAITER {}] No soup left", id);
                    queue.poll();
                    return SoupState.NoPortionsLeft;
                }
            }

            queue.poll();
            return SoupState.Ok;
        }

        return SoupState.Wait;
    }
}
