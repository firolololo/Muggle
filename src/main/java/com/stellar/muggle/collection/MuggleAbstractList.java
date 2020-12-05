package com.stellar.muggle.collection;

import java.util.Iterator;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:45
 */
public abstract class MuggleAbstractList<T> implements MuggleList<T> {
    protected transient int modCount = 0;

    @Override
    abstract public Iterator<T> iterator();

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
    abstract public int size();

    @Override
    abstract public MuggleList<T> subList(int start, int end);

    @Override
    public abstract T[] toArray();

    @Override
    public int indexOf(T t) {
        Iterator<T> iterator = iterator();
        int index = -1;
        if (t == null) {
            while (iterator.hasNext()) {
                T cur = iterator.next();
                index++;
                if (cur == null) return index;
            }
        } else {
            while (iterator.hasNext()) {
                T cur = iterator.next();
                index++;
                if (t.equals(cur)) return index;
            }
        }

        return index;
    }

    @Override
    public int lastIndexOf(T t) {
        Iterator<T> iterator = iterator();
        int index = -1;
        int res = -1;
        if (t == null) {
            while (iterator.hasNext()) {
                T cur = iterator.next();
                index++;
                if (cur == null) res = index;
            }
        } else {
            while (iterator.hasNext()) {
                T cur = iterator.next();
                index++;
                if (t.equals(cur)) res = index;
            }
        }
        return res;
    }

    @Override
    public boolean add(T t) {
        add(size(), t);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(MuggleCollection<T> collection) {
        Iterator<T> itr = (Iterator<T>)collection.iterator();
        while (itr.hasNext()) {
            add(itr.next());
        }
        return true;
    }

    @Override
    public boolean remove(T t) {
        Iterator<T> itr = iterator();
        if (t == null) {
            while (itr.hasNext()) {
                T cur = itr.next();
                if (cur == null) itr.remove();
            }
        } else {
            while (itr.hasNext()) {
                T cur = itr.next();
                if (t.equals(cur)) itr.remove();
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(MuggleCollection<T> collection) {
        Iterator<T> itr = (Iterator<T>)collection.iterator();
        while (itr.hasNext()) {
            remove(itr.next());
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(T t) {
        Iterator<T> itr = iterator();
        if (t == null) {
            while (itr.hasNext()) {
                if (itr.next() == null) return true;
            }
        } else {
            while (itr.hasNext()) {
                if (t.equals(itr.next())) return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(MuggleCollection<T> collection) {
        Iterator<T> itr = (Iterator<T>)collection.iterator();
        boolean res = true;
        while (itr.hasNext()) {
            res = res && contains(itr.next());
        }
        return res;
    }

    @Override
    public void clear() {
        Iterator itr = iterator();
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }

    void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new RuntimeException("Illegal index");
        }
    }

    void checkIndexForAdd(int index) {
        if (index < 0 || index > size()) {
            throw new RuntimeException("Illegal index");
        }
    }


    class Itr implements Iterator<T> {
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
        public void remove() {
            if (lastRet < 0) {
                throw new RuntimeException("Illegal remove");
            }
            try {
                MuggleAbstractList.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
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
