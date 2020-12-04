package com.stellar.muggle.list;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:17
 */

public interface MuggleList<T> extends MuggleCollection<T> {
    T get(int index);
    T set(int index, T t);
    T remove(int index);
    void add(int index, T t);
    int indexOf(T t);
    int lastIndexOf(T t);
    MuggleList<T> subList(int start, int end);
}
