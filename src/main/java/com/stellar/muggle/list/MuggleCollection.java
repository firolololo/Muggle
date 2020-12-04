package com.stellar.muggle.list;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:01
 */
public interface MuggleCollection<T> extends MuggleIterable<T> {
    int size();
    boolean add(T t);
    boolean addAll(MuggleCollection<T> collection);
    boolean remove(T t);
    boolean removeAll(MuggleCollection<T> collection);
    boolean isEmpty();
    boolean contains(T t);
    boolean containsAll(MuggleCollection<T> collection);
    T[] toArray();
}
