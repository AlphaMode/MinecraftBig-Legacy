package me.alphamode.mcbig.world.level;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LightExecutor extends Thread {

    private final Lock lock = new ReentrantLock();

    @Override
    public void run() {

    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
