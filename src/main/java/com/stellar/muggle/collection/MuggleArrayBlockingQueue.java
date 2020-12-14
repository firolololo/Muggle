package com.stellar.muggle.collection;

import com.stellar.muggle.lock.MuggleReentrantLock;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/14 10:30
 */
public class MuggleArrayBlockingQueue<T> extends MuggleAbstractQueue<T> implements MuggleBlockQueue<T>, java.io.Serializable {
    Object[] array;
    int putIndex;
    int takeIndex;
    int count;
    private final transient MuggleReentrantLock lock;
    private final transient Condition notEmpty;
    private final transient Condition notFull;

    public MuggleArrayBlockingQueue(int capacity, boolean fair) {
        lock = new MuggleReentrantLock();
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
        array = new Object[capacity];
    }


    @Override
    public boolean offer(T t) {
        checkNotNull(t);
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == array.length) return false;
            enqueue(t);
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
            if (count == 0) return null;
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(T t) throws InterruptedException {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            while (count == array.length) {
                notFull.await();
            }
            enqueue(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T take() throws InterruptedException {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        final MuggleReentrantLock lock = this.lock;
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (count == array.length && nanos > 0) {
                nanos = notFull.awaitNanos(nanos);
            }
            if (count == array.length) return false;
            offer(t);
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        final MuggleReentrantLock lock = this.lock;
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (count == 0 && nanos > 0) {
                nanos = notEmpty.awaitNanos(nanos);
            }
            if (count == 0) return null;
            return poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int drainTo(MuggleCollection<? super T> c) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            final int putIndex = this.putIndex;
            int index = takeIndex;
            int nums = 0;
            while (index != putIndex) {
                c.add(dequeue());
                index++;
                nums++;
                if (index == array.length) index = 0;
            }
            return nums;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        return (T)array[takeIndex];
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean remove(T t) {
        checkNotNull(t);
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            int index = this.takeIndex;
            final int putIndex = this.putIndex;
            while (index != putIndex) {
                if (t.equals(array[index])) break;
                index++;
                if (index == array.length) index = 0;
            }
            if (index == putIndex) return false;
            if (index == this.takeIndex) {
                dequeue();
            } else {
                for (int i = index;;) {
                    int next = i + 1;
                    if (next == array.length) next = 0;
                    array[i] = array[next];
                    if (next == putIndex) {
                        this.putIndex = i;
                        count--;
                        notFull.signal();
                        break;
                    }
                    i = next;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(T t) {
        checkNotNull(t);
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            final int putIndex = this.putIndex;
            int index = this.takeIndex;
            while (index != putIndex) {
                if (t.equals(array[index])) return true;
                index++;
                if (index == array.length) index = 0;
            }
            return false;
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
            Object[] a = new Object[count];
            int n = array.length - takeIndex;
            if (n <= count) {
                System.arraycopy(array, takeIndex, a, 0, count);
            } else {
                System.arraycopy(array, takeIndex, a, 0, n);
                System.arraycopy(array, 0, a, n, count - n);
            }
            return (T[])a;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr(toArray());
    }

    class Itr implements Iterator<T> {
        private Object[] array;
        private int cursor;

        Itr(T[] array) {
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
            return (T)array[cursor++];
        }

        @Override
        public void remove() {
            throw new RuntimeException("Unsupport Operation");
        }
    }

    private void checkNotNull(T t) {
        if (t == null) throw new RuntimeException("Null element");
    }

    private void enqueue(T t) {
        array[putIndex++] = t;
        if (putIndex == array.length) putIndex = 0;
        count++;
        notEmpty.signal();
    }

    @SuppressWarnings("unchecked")
    private T dequeue() {
        T value = (T)array[takeIndex++];
        if (takeIndex == array.length) takeIndex = 0;
        count--;
        notFull.signal();
        return value;
    }
}
