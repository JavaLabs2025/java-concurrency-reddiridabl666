package dws.labs.lunch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;

public class Spoon {
    private final Lock lock;

    @Getter
    private final int id;

    public Spoon(int id) {
        this.lock = new ReentrantLock(true);
        this.id = id;
    }

    void take() {
        lock.lock();
    }

    void leave() {
        lock.unlock();
    }
}
