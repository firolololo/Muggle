package com.stellar.muggle.list;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:45
 */
public abstract class MuggleAbstractList<T> implements MuggleList<T> {
    protected transient int modCount = 0;

    @Override
    abstract public MuggleIterator<T> iterator();

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    abstract public T get(int index);

    @Override
    abstract public T set(int index, T t);

    @Override
    abstract public T remove(int index);

    @Override
    abstract public void add(int index, T t);

    @Override
    public int indexOf(T t) {
        return 0;
    }

    @Override
    public int lastIndexOf(T t) {
        return 0;
    }

    @Override
    public MuggleList<T> subList(int start, int end) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean addAll(MuggleCollection<T> collection) {
        return false;
    }

    @Override
    public boolean remove(T t) {
        return false;
    }

    @Override
    public boolean removeAll(MuggleCollection<T> collection) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(T t) {
        return false;
    }

    @Override
    public boolean containsAll(MuggleCollection<T> collection) {
        return false;
    }

    @Override
    public T[] toArray() {
        return null;
    }

    class Itr implements MuggleIterator<T> {
        int cursor = 0;
        int lastRet = -1;
        int expectedModCount = modCount;
        @Override
        public boolean hasNext() {
            return this.cursor != size();
        }

        @Override
        public T next() {
            checkForComodification();
            try {
                int i = cursor;
                T next = get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (RuntimeException ex) {
                checkForComodification();
                throw new RuntimeException("NoSuchElementException");
            }
        }

        @Override
        public T remove() {
            if (lastRet < 0) {
                throw new RuntimeException("Illegal remove");
            }
            try {
                T item = MuggleAbstractList.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
                return item;
            } catch (RuntimeException ex) {
                checkForComodification();
                throw new RuntimeException("NoSuchElementException");
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new RuntimeException("ConcurrentModificationException");
        }
    }
}
