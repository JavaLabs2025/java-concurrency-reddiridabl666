package dws.labs.lunch;

import java.util.concurrent.atomic.AtomicInteger;

public class Soup {
    private final AtomicInteger portionsLeft;

    public Soup(int amount) {
        this.portionsLeft = new AtomicInteger(amount);
    }

    void getPortion() {
        portionsLeft.decrementAndGet();
    }

    int getPortionsLeft() {
        return portionsLeft.get();
    }
}
