package com.stellar.muggle.collection;

import com.stellar.muggle.lock.MuggleReentrantLock;
import org.omg.CORBA.Object;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/14 15:56
 */
public class MuggleLinkedBlockingQueue<T> extends MuggleAbstractQueue<T> implements MuggleBlockQueue<T>, java.io.Serializable {
    static class Node<T> {
        T element;
        Node<T> next;
        Node(T element) {
            this.element = element;
        }
    }

    private transient Node<T> head;
    private transient Node<T> tail;
    private MuggleReentrantLock putLock = new MuggleReentrantLock();
    private Condition notFull = putLock.newCondition();
    private MuggleReentrantLock takeLock = new MuggleReentrantLock();
    private Condition notEmpty = takeLock.newCondition();

    private final int capacity;
    private AtomicInteger count = new AtomicInteger();

    public MuggleLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    public MuggleLinkedBlockingQueue(int capacity) {
        this.capacity = capacity;
        head = tail = new Node<T>(null);
    }

    @Override
    public void put(T t) throws InterruptedException {
        checkNotNull(t);
        final MuggleReentrantLock putLock = this.putLock;
        putLock.lock();
        int c;
        Node<T> node = new Node<>(t);
        try {
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
    }

    @Override
    public T take() throws InterruptedException {
        final MuggleReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        int c;
        T value;
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            value = dequeue();
            c = count.getAndDecrement();
            if (c - 1 > 0) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return value;
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        checkNotNull(t);
        putLock.lock();
        final MuggleReentrantLock putLock = this.putLock;
        int c;
        Node<T> node = new Node<>(t);
        long nanos = unit.toNanos(timeout);
        try {
            while (count.get() == capacity && nanos > 0) {
                nanos = notFull.awaitNanos(nanos);
            }
            if (count.get() == capacity) return false;
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        final MuggleReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        int c;
        T value;
        long nanos = unit.toNanos(timeout);
        try {
            while (count.get() == 0 && nanos > 0) {
                nanos = notEmpty.awaitNanos(nanos);
            }
            if (count.get() == 0) return null;
            value = dequeue();
            c = count.getAndDecrement();
            if (c - 1 > 0) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int drainTo(MuggleCollection<? super T> c) {
        this.fullyLock();
        int n = 0;
        try {
            Node<T> h = head;
            while (h.next != null) {
                Node<T> next = h.next;
                T value = (T)next.element;
                h.next = h;
                count.getAndDecrement();
                c.add(value);
                n++;
            }
        } finally {
            this.fullyUnlock();
        }
        return n;
    }

    @Override
    public boolean offer(T t) {
        checkNotNull(t);
        final MuggleReentrantLock putLock = this.putLock;
        putLock.lock();
        int c;
        Node<T> node = new Node<>(t);
        try {
            if (count.get() == capacity) return false;
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public T poll() {
        final MuggleReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        int c;
        T value;
        try {
            if (count.get() == 0) return null;
            value = dequeue();
            c = count.getAndDecrement();
            if (c - 1 > 0) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return value;
    }

    @Override
    public T peek() {
        return head.next == null ? null : head.next.element;
    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public boolean remove(T t) {
        checkNotNull(t);
        this.fullyLock();
        try {
            Node<T> h = head;
            while (h != tail) {
                Node<T> next = h.next;
                if (t.equals(next.element)) {
                    if (next == tail) tail = h;
                    h.next = next.next;
                    next.next = next;
                    count.getAndDecrement();
                    return true;
                }
                h = next;
            }
        } finally {
            this.fullyUnlock();
        }
        return false;
    }

    @Override
    public boolean contains(T t) {
        checkNotNull(t);
        this.fullyLock();
        try {
            Node<T> h = head;
            while (h != tail) {
                Node<T> next = h.next;
                if (t.equals(next.element)) {
                    return true;
                }
                h = next;
            }
        } finally {
            this.fullyUnlock();
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        this.fullyLock();
        T[] a = (T[])new Object[count.get()];
        try {
            Node<T> h = head;
            int index = 0;
            while (h != tail) {
                Node<T> next = h.next;
                a[index++] = next.element;
                h = next;
            }
        } finally {
            this.fullyUnlock();
        }
        return a;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    class Itr implements Iterator<T> {
        private Node<T> current;
        private Node<T> lastRet;
        private T currentElement;

        Itr() {
            fullyLock();
            try {
                current = head.next;
                if (current != null) currentElement = current.element;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        private Node<T> nextNode(Node<T> p) {
            for (;;) {
                Node<T> s = p.next;
                if (s == p) return head.next;
                if (s == null || s.element != null) return s;
                p = s;
            }
        }

        @Override
        public T next() {
            fullyLock();
            try {
                if (current == null) throw new RuntimeException("No element");
                T x = currentElement;
                lastRet = current;
                current = nextNode(current);
                currentElement = current == null ? null : current.element;
                return x;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public void remove() {
            fullyLock();
            try {
                if (lastRet == null) throw new RuntimeException("Illegal state");
                Node<T> p = lastRet;
                lastRet = null;
                Node<T> h = head;
                while (h != tail) {
                    Node<T> next = h.next;
                    if (next == p) {
                        if (next == tail) tail = h;
                        h.next = next.next;
                        next.next = next;
                        count.getAndDecrement();
                        break;
                    }
                    h = next;
                }
            } finally {
                fullyUnlock();
            }
        }
    }

    private void signalNotEmpty() {
        final MuggleReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    private void signalNotFull() {
        final MuggleReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    private void fullyLock() {
        this.putLock.lock();
        this.takeLock.lock();
    }

    private void fullyUnlock() {
        this.putLock.unlock();
        this.takeLock.unlock();
    }

    private void enqueue(Node<T> node) {
        tail.next = node;
        tail = node;
    }

    private T dequeue() {
        Node<T> h = head;
        Node<T> first = h.next;
        h.next = h;
        T value = first.element;
        first.element = null;
        head = first;
        return value;
    }

    private void checkNotNull(T t) {
        if (t == null) throw new RuntimeException("Null element");
    }
}
