package com.stellar.muggle.tool;

import com.stellar.muggle.collection.MuggleBlockQueue;
import com.stellar.muggle.collection.MuggleCollection;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author firo
 * @version 1.0
 * @date 2021/1/5 17:30
 */
public class MuggleMpscLinkedQueue<T> implements MuggleBlockQueue<T> {
    private volatile AtomicInteger count;

    static class MpscLinkedNode<T> {
        private T value;
        private MpscLinkedNode<T> next;
        MpscLinkedNode(T value, MpscLinkedNode<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    MpscLinkedNode<T> head;
    MpscLinkedNode<T> tail;

    MpscLinkedNode<T> defaultNode(T t) {
        return new MpscLinkedNode<>(t, null);
    }

    public MuggleMpscLinkedQueue() {
        head = defaultNode(null);
        tail = head;
    }

    @SuppressWarnings("unchecked")
    private MpscLinkedNode<T> replaceTail(MpscLinkedNode<T> node) {
        return (MpscLinkedNode<T>)UNSAFE.getAndSetObject(tail, tailOffset, node);
    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public boolean add(T t) {
        Objects.requireNonNull(t);
        final MpscLinkedNode<T> newTail = defaultNode(t);
        MpscLinkedNode<T> oldTail = replaceTail(newTail);
        oldTail.next = newTail;
        count.incrementAndGet();
        return true;
    }

    @Override
    public boolean addAll(MuggleCollection<T> collection) {
        Objects.requireNonNull(collection);
        for (T value: collection)
            add(value);
        return true;
    }

    @Override
    public boolean remove(T t) {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public boolean removeAll(MuggleCollection<T> collection) {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public boolean contains(T t) {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public boolean containsAll(MuggleCollection<T> collection) {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public T[] toArray() {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public boolean offer(T t) {
        return add(t);
    }

    @Override
    public T poll() {
        if (isEmpty()) return null;
        head = head.next;
        count.decrementAndGet();
        T value = head.value;
        head.value = null;
        return value;
    }

    @Override
    public T remove() {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public T peek() {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public T element() {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public void put(T t) throws InterruptedException {
        add(t);
    }

    @Override
    public T take() throws InterruptedException {
        return null;
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Unsupport operation");
    }

    @Override
    public int drainTo(MuggleCollection<? super T> c) {
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;

    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = MuggleMpscLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
