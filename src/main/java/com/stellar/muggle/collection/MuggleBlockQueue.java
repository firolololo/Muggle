package com.stellar.muggle.collection;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 11:09
 */
public interface MuggleBlockQueue<T> extends MuggleQueue<T> {
    boolean offer(T t);
    T poll();
    void put(T t) throws InterruptedException;
    T take() throws InterruptedException;
    boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException;
    T poll(long timeout, TimeUnit unit) throws InterruptedException;
    int drainTo(MuggleCollection<? super T> c);
}
