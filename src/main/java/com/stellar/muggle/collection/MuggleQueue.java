package com.stellar.muggle.collection;

/**
 * Created by wanglecheng on 2020/12/5.
 */
public interface MuggleQueue<T> extends MuggleCollection<T>{
    boolean add(T t);

    boolean offer(T t);

    T poll();

    T remove();

    T peek();

    T element();
}
