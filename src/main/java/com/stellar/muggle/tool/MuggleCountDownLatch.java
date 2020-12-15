package com.stellar.muggle.tool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/15 14:59
 */
public class MuggleCountDownLatch {

    private Sync sync;

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) { setState(count); }

        int getCount() {return getState();}

        @Override
        protected int tryAcquireShared(int arg) {
            return (getCount() == 0) ? 1: -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            for (;;) {
                int current = getState();
                if (current == 0) return false;
                int next = current - 1;
                if (compareAndSetState(current, next)) {
                    return next == 0;
                }
            }
        }
    }

    public MuggleCountDownLatch(int count) { this.sync = new Sync(count); }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public long getCount() {
        return sync.getCount();
    }

}
