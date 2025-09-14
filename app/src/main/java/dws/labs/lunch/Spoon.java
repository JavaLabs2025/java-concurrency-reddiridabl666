package dws.labs.lunch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;

public class Spoon {
    private final Lock lock;

    @Getter
    private final int id;

    public Spoon(int id) {
        this.lock = new ReentrantLock();
        this.id = id;
    }

    boolean take() throws InterruptedException {
        return lock.tryLock(10, TimeUnit.MICROSECONDS);
    }

    void leave() {
        lock.unlock();
    }
}
