package com.stellar.muggle.collection;

import com.stellar.muggle.lock.MuggleReentrantLock;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 11:39
 */
public class MuggleDelayQueue<T extends Delayed> extends MuggleAbstractQueue<T> implements MuggleBlockQueue<T> {
    private final PriorityQueue<T> pq = new PriorityQueue<>();
    private final MuggleReentrantLock lock = new MuggleReentrantLock();
    private final Condition available = lock.newCondition();
    private Thread leader;

    @Override
    public void put(T t) {
        offer(t);
    }

    @Override
    public T take() throws InterruptedException {
        final MuggleReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                T first = pq.peek();
                if (first == null) {
                    available.await();
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if (delay <= 0) return pq.poll();
                    first = null;
                    if (leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && pq.peek() != null)
                available.signal();
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) {
        return offer(t);
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final MuggleReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                T first = pq.peek();
                if (first == null) {
                    if (nanos <= 0) return null;
                    nanos = available.awaitNanos(nanos);
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if (delay <= 0) return pq.poll();
                    if (nanos <= 0) return null;
                    first = null;
                    if (nanos < delay || leader != null) {
                        available.awaitNanos(nanos);
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            long timeLeft = available.awaitNanos(delay);
                            nanos = nanos - (delay - timeLeft);
                        } finally {
                            if (leader == thisThread) leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && pq.peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            pq.offer(t);
            if (pq.peek() == t) {
                leader = null;
                available.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T poll() {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            T t = pq.peek();
            if (t != null && t.getDelay(TimeUnit.NANOSECONDS) > 0) {
                return pq.poll();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peek() {
        return pq.peek();
    }

    @Override
    public int size() {
        return pq.size();
    }


    @Override
    public boolean remove(T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            return pq.remove(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            return pq.contains(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (T[])pq.toArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr(toArray());
    }

    @Override
    public int drainTo(MuggleCollection<? super T> c) {
        if (c == null) throw new RuntimeException("Null collection");
        if (c == this) throw new RuntimeException("Invalid collection");
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            for (T t; (t = peekExpired()) != null; ) {
                c.add(t);
                poll();
                n++;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    private T peekExpired() {
        // assert lock.isHeldByCurrentThread();
        T first = pq.peek();
        return (first == null || first.getDelay(NANOSECONDS) > 0) ?
                null : first;
    }

    private void removeEQ(Object o) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (Iterator<T> itr = pq.iterator(); itr.hasNext(); ) {
                if (o == itr.next()) {
                    itr.remove();
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    class Itr implements Iterator<T> {
        final Object[] array;
        int cursor;
        int lastRet;

        Itr(Object[] array) {
            lastRet = -1;
            this.array = array;
        }
        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (cursor >= array.length) throw new RuntimeException("Invalid index");
            lastRet = cursor;
            return (T)array[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0) throw new RuntimeException("Invalid lastRet");
            removeEQ(array[lastRet]);
            lastRet = -1;
        }
    }
}
