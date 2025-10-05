package dws.labs.lunch;

import java.util.concurrent.atomic.AtomicInteger;

public class Soup {
    private final AtomicInteger portionsLeft;

    public Soup(int amount) {
        this.portionsLeft = new AtomicInteger(amount);
    }

    boolean getPortion() {
        int current = portionsLeft.get();
        if (current <= 0) {
            return false;
        }

        return portionsLeft.compareAndSet(current, current - 1);
    }

    int getPortionsLeft() {
        return portionsLeft.get();
    }
}
