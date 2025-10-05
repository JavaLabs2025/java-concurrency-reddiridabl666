package dws.labs.lunch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class WaiterBell {
    private final Queue<Programmer> queue;
    private final List<Boolean> programmersFinished;

    public WaiterBell(int programmersNum) {
        queue = new ArrayDeque<>(programmersNum);
        programmersFinished = new ArrayList<>(Collections.nCopies(programmersNum, false));
    }

    synchronized void ring(Programmer programmer) {
        queue.add(programmer);
    }

    void programmerFinised(int id) {
        programmersFinished.set(id, true);
    }

    boolean haveAllProgrammersFinished() {
        return programmersFinished.stream().allMatch(Boolean::booleanValue);
    }

    synchronized Programmer poll() {
        var programmer = queue.peek();
        if (programmer == null || !programmer.isReadyForFood()) {
            return null;
        }
        return queue.poll();
    }
}
