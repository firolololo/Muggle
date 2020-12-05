package com.stellar.muggle.collection;

/**
 * Created by wanglecheng on 2020/12/5.
 */
public interface MuggleDeque<T> extends MuggleQueue<T> {
    boolean addFirst(T t);

    boolean addLast(T t);

    boolean offerFirst(T t);

    boolean offerLast(T t);

    T pollFirst();

    T pollLast();

    T removeFirst();

    T removeLast();

    T peekFirst();

    T peekLast();

    T elementFirst();

    T elementLast();
}
