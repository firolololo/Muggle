package com.stellar.muggle.collection;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 15:46
 */
public abstract class MuggleAbstractCollection<T> implements MuggleCollection<T> {

    @Override
    public boolean removeAll(MuggleCollection<T> collection) {
        for (T t: collection) {
            remove(t);
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsAll(MuggleCollection<T> collection) {
        for(T t: collection) {
            if (!contains(t)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(MuggleCollection<T> collection) {
        for (T t: collection) {
            add(t);
        }
        return true;
    }
}
