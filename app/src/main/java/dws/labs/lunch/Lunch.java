package dws.labs.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Lunch {
    private final List<Programmer> programmers;

    private final List<Spoon> spoons;

    private final Soup soup;

    public Lunch(int programmersNum, int soupAmount, int bowlSize, int programmerSoupNeed) {
        assert programmersNum > 1;

        this.soup = new Soup(soupAmount);

        this.spoons = IntStream.range(0, programmersNum)
                .mapToObj(i -> new Spoon(i))
                .toList();

        this.programmers = new ArrayList<>(programmersNum);

        for (int i = 0; i < programmersNum - 1; ++i) {
            var currentProgrammerSpoons = List.of(spoons.get(i), spoons.get(i + 1));

            this.programmers.add(new Programmer(i, currentProgrammerSpoons, soup, programmerSoupNeed, bowlSize));
        }

        var lastProgrammerSpoons = List.of(spoons.getLast(), spoons.getFirst());

        this.programmers
                .add(new Programmer(programmersNum - 1, lastProgrammerSpoons, soup, programmerSoupNeed, bowlSize));
    }

    public void run() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (var programmer : programmers) {
            var thread = new Thread(programmer);
            threads.add(thread);
            thread.start();
        }

        for (var thread : threads) {
            thread.join();
        }
    }
}
