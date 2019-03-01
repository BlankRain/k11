package filedb;

import java.util.concurrent.locks.StampedLock;

/**
 * 假锁.
 */
public class FeakLock {
    private StampedLock lock = new StampedLock();

    public void unlockWrite(long stamp) {
        lock.unlockWrite(stamp);
    }

    public long writeLock() {
        return lock.writeLock();
    }
}
