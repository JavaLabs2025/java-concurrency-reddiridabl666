package dws.labs.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Lunch {
    private final List<Spoon> spoons;

    private final Soup soup;

    private final int programmerSoupNeed;

    public Lunch(int programmersNum, int soupAmount, int programmerSoupNeed) {
        assert programmersNum > 1;

        this.soup = new Soup(soupAmount);

        this.spoons = IntStream.range(0, programmersNum)
                .mapToObj(i -> new Spoon(i))
                .toList();

        this.programmerSoupNeed = programmerSoupNeed;
    }

    public boolean run(long timeout, TimeUnit unit) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(spoons.size());

        var programmers = createProgrammers(latch);

        for (var programmer : programmers) {
            var thread = new Thread(programmer);
            threads.add(thread);
            thread.start();
        }

        return latch.await(timeout, unit);
    }

    private List<Programmer> createProgrammers(CountDownLatch latch) {
        var programmersNum = spoons.size();

        var programmers = new ArrayList<Programmer>();

        for (int i = 0; i < programmersNum - 1; ++i) {
            var currentProgrammerSpoons = List.of(spoons.get(i), spoons.get(i + 1));

            programmers.add(new Programmer(i, latch, currentProgrammerSpoons, soup, programmerSoupNeed));
        }

        var lastProgrammerSpoons = List.of(spoons.getLast(), spoons.getFirst());

        programmers.add(
                new Programmer(programmersNum - 1, latch, lastProgrammerSpoons, soup, programmerSoupNeed));

        return programmers;
    }
}
