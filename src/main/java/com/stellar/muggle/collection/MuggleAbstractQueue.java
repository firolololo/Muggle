package com.stellar.muggle.collection;

import java.util.Iterator;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 11:41
 */
public abstract class MuggleAbstractQueue<T> extends MuggleAbstractCollection<T> implements MuggleQueue<T> {

    @Override
    public boolean add(T t) {
        return offer(t);
    }

    @Override
    public T remove() {
        T t = peek();
        if (t != null) return poll();
        throw new RuntimeException("No element");
    }

    @Override
    public T element() {
        T t = peek();
        if (t != null) return peek();
        throw new RuntimeException("No element");
    }

    @Override
    public void clear() {
        while (poll() != null);
    }
}
