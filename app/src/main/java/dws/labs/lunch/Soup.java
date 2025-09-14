package dws.labs.lunch;

import java.util.concurrent.atomic.AtomicInteger;

public class Soup {
    private final AtomicInteger amountLeft;

    public Soup(int amount) {
        this.amountLeft = new AtomicInteger(amount);
    }

    boolean get(int amount) {
        int current = amountLeft.get();
        return amountLeft.compareAndSet(current, current - amount);
    }

    int current() {
        return amountLeft.get();
    }
}
