package dws.labs.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lunch {
    private final List<Spoon> spoons;

    private final List<Waiter> waiters;

    private final Soup soup;

    private final int programmersNum;

    public Lunch(int programmersNum, int waitersNum, int soupAmount) {
        assert programmersNum > 1;

        this.programmersNum = programmersNum;

        this.soup = new Soup(soupAmount);

        this.spoons = IntStream.range(0, programmersNum)
                .mapToObj(i -> new Spoon(i))
                .toList();

        this.waiters = IntStream.range(0, waitersNum)
                .mapToObj(i -> new Waiter(i, soup, programmersNum))
                .toList();
    }

    public boolean run(long timeout, TimeUnit unit) throws InterruptedException {
        List<Thread> threads = new ArrayList<>(programmersNum);

        CountDownLatch latch = new CountDownLatch(programmersNum);

        var programmers = createProgrammers(latch);

        for (var programmer : programmers) {
            var thread = new Thread(programmer);
            threads.add(thread);
            thread.start();
        }

        log.info("Started {} programmer threads", programmers.size());

        var result = latch.await(timeout, unit);

        log.info("Soup left: {}", soup.getPortionsLeft());

        log.info("Programmers ate: {}", programmers.stream()
                .map(programmer -> programmer.getAteSoupPortions())
                .toList());

        if (soup.getPortionsLeft() != 0) {
            throw new RuntimeException(String.format("soup left: %d, expected 0", soup.getPortionsLeft()));
        }
        return result;
    }

    private List<Programmer> createProgrammers(CountDownLatch latch) {
        var programmers = new ArrayList<Programmer>();

        for (int i = 0; i < programmersNum - 1; ++i) {
            var currentProgrammerSpoons = List.of(spoons.get(i), spoons.get(i + 1));

            programmers.add(new Programmer(i, latch, currentProgrammerSpoons, waiters));
        }

        var lastProgrammerSpoons = List.of(spoons.getFirst(), spoons.getLast());

        programmers.add(
                new Programmer(programmersNum - 1, latch, lastProgrammerSpoons, waiters));

        return programmers;
    }
}
