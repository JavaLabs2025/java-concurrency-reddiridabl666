package dws.labs.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lunch {
    public record Metrics(boolean timeoutExceeded, int soupLeft, List<Integer> errors) {}

    private final List<Spoon> spoons;

    private final Soup soup;

    private final int programmersNum;

    private final int waitersNum;

    private final int totalPortions;

    public Lunch(int programmersNum, int waitersNum, int soupAmount) {
        assert programmersNum > 1;

        this.programmersNum = programmersNum;
        this.waitersNum = waitersNum;
        this.totalPortions = soupAmount;

        this.soup = new Soup(soupAmount);

        this.spoons = IntStream.range(0, programmersNum).mapToObj(Spoon::new).toList();

    }

    public Metrics run(long timeout, TimeUnit unit) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(programmersNum + waitersNum);

        var programmers = createProgrammers(latch);

        var waiters = IntStream.range(0, waitersNum)
                .mapToObj(i -> new Waiter(i, latch, soup, programmers))
                .toList();

        for (var programmer : programmers) {
            var thread = new Thread(programmer);
            thread.start();
        }

        for (var waiter : waiters) {
            var thread = new Thread(waiter);
            thread.start();
        }

        log.info("Started {} programmer threads", programmers.size());

        var latchResult = latch.await(timeout, unit);

        log.info("Soup left: {}", soup.getPortionsLeft());

        log.info("Programmers ate: {}", programmers.stream().map(programmer -> programmer.getAteSoupPortions()).toList());
        log.info("Programmers ate total: {}", programmers.stream()
                .map(programmer -> programmer.getAteSoupPortions())
                .reduce((a, b) -> a + b)
                .get());

        double mean = (double) totalPortions / programmersNum;

        log.info("Mean: {}", mean);

        var errors = programmers.stream()
                .map(programmer -> Math.abs(mean - programmer.getAteSoupPortions())
                        / (double) programmer.getAteSoupPortions() * 100)
                .map(error -> (int) Math.round(error))
                .toList();

        log.info("Programmers error % from expected mean: {}", errors.stream().map(error -> error + "%").toList());

        return new Metrics(latchResult, soup.getPortionsLeft(), errors);
    }

    private List<Programmer> createProgrammers(CountDownLatch latch) {
        var programmers = new ArrayList<Programmer>();

        for (int i = 0; i < programmersNum - 1; ++i) {
            var currentProgrammerSpoons = List.of(spoons.get(i), spoons.get(i + 1));

            programmers.add(new Programmer(i, latch, currentProgrammerSpoons));
        }

        var lastProgrammerSpoons = List.of(spoons.getFirst(), spoons.getLast());

        programmers.add(new Programmer(programmersNum - 1, latch, lastProgrammerSpoons));

        return programmers;
    }
}
