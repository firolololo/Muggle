package com.stellar.muggle.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/10 14:16
 */
public class MuggleReentrantLock implements Lock, java.io.Serializable {

    private Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {
        abstract void lock();

        final boolean nonfairTryAcquire(int arg) {
            final Thread thread = Thread.currentThread();
            int c = getState();
            if (c == 0 && compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            } else if (getExclusiveOwnerThread() == thread) {
                int nextc = c + arg;
                if (nextc < 0) throw new RuntimeException("maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            final Thread thread = Thread.currentThread();
            if (getExclusiveOwnerThread() == thread) {
                int c = getState() - arg;
                if (c == 0) {
                    setExclusiveOwnerThread(null);
                    setState(c);
                    return true;
                }
                setState(c);
            }
            return false;
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        final boolean isLocked() { return getState() != 0; }
        final Thread getOwner() { return getExclusiveOwnerThread(); }
    }

    static final class NonfairSync extends Sync {

        @Override
        void lock() {
            acquire(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return nonfairTryAcquire(1);
        }
    }

    static final class FairSync extends Sync {

        @Override
        void lock() {
            acquire(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            final Thread thread = Thread.currentThread();
            int c = getState();
            if (c == 0 && !hasQueuedPredecessors() && compareAndSetState(c, arg)) {
                setExclusiveOwnerThread(thread);
                return true;
            } else if (thread == getExclusiveOwnerThread()) {
                int nextc = c + arg;
                if (nextc < 0) throw new RuntimeException("maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    public MuggleReentrantLock() {
        this.sync = new NonfairSync();
    }

    public MuggleReentrantLock(boolean fair) { this.sync = fair ? new FairSync() : new NonfairSync(); }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean isLocked() {
        return sync.isLocked();
    }

    public Thread getOwner() {
        return sync.getOwner();
    }
}
