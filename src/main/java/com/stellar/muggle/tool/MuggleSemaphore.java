package com.stellar.muggle.tool;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/15 13:37
 */
public class MuggleSemaphore implements java.io.Serializable {
    private Sync sync;

    static abstract class Sync extends AbstractQueuedSynchronizer {
        Sync(int state) { setState(state);}

        final int getPermits() { return getState(); }

        final int nonfairTryAcquireShared(int acquires) {
            for (;;) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining)) {
                    return remaining;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int current = getState();
                int next = current + releases;
                if (next < current) throw new RuntimeException("Invalid releases");
                if (compareAndSetState(current, next)) return true;
            }
        }

        final void reducePermits(int reductions) {
            for (;;) {
                int current = getState();
                int next = current + reductions;
                if (next < current) throw new RuntimeException("Invalid reductions");
                if (compareAndSetState(current, next)) return;
            }
        }

        final int drainPermits() {
            for (;;) {
                int current = getState();
                if (current == 0 || compareAndSetState(current, 0)) {
                    return current;
                }
            }
        }
    }

    static class NonfairSync extends Sync {

        NonfairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
        }
    }

    static class FairSync extends Sync {

        FairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            for (;;) {
                if (hasQueuedPredecessors()) return -1;
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining)) {
                    return remaining;
                }
            }
        }
    }

    public MuggleSemaphore(int permits) {
        sync = new NonfairSync(permits);
    }

    public MuggleSemaphore(int permits, boolean fair) {
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }

    public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public void acquire(int permits) throws InterruptedException {
        if (permits < 0) throw new IllegalArgumentException();
        sync.acquireSharedInterruptibly(permits);
    }

    public void acquireUninterruptibly() {
        sync.acquireShared(1);
    }

    public boolean tryAcquire() {
        return sync.nonfairTryAcquireShared(1) >= 0;
    }

    public boolean tryAcquire(int permits) {
        if (permits < 0) throw new IllegalArgumentException();
        return sync.nonfairTryAcquireShared(permits) >= 0;
    }

    public void release(int permits) {
        if (permits < 0) throw new IllegalArgumentException();
        sync.releaseShared(permits);
    }

    public void release() {
        sync.releaseShared(1);
    }
}
