package com.stellar.muggle.collection;

import com.stellar.muggle.lock.MuggleReentrantLock;

import java.util.Arrays;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/10 15:48
 */
public class MuggleCopyOnWirteArrayList<T> implements MuggleList<T>, RandomAccess, Cloneable, java.io.Serializable {
    private transient volatile Object[] array;
    private transient MuggleReentrantLock lock = new MuggleReentrantLock();

    final Object[] getArray() { return array; }
    final void setArray(Object[] a) { array = a; }

    public MuggleCopyOnWirteArrayList() { setArray(new Object[0]); }

    private MuggleCopyOnWirteArrayList(Object[] init) {
        setArray(init);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T)array[index];
    }

    @Override
    public T set(int index, T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            checkIndex(index);
            Object[] newArray = Arrays.copyOf(array, size());
            T oldValue = get(index);
            if (!oldValue.equals(newArray[index])) {
                newArray[index] = t;
            }
            setArray(newArray);
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T remove(int index) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            checkIndex(index);
            T value = get(index);
            int moves = size() - index - 1;
            if (moves == 0) {
                setArray(Arrays.copyOf(array, size() - 1));
            } else {
                Object[] newArray = new Object[size() - 1];
                System.arraycopy(array, 0, newArray, 0, index);
                System.arraycopy(array, index + 1, newArray, index, moves);
                setArray(newArray);
            }
            return value;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(int index, T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            checkIndexForAdd(index);
            int moves = size() - index;
            Object[] newArray;
            if (moves == 0) {
                newArray = Arrays.copyOf(array, size() + 1);
            } else {
                newArray = new Object[size() + 1];
                System.arraycopy(array, 0, newArray, 0, index);
                System.arraycopy(array, index, newArray, index + 1, moves);
            }
            newArray[index] = t;
            setArray(newArray);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MuggleList<T> subList(int start, int end) {
        Object[] snapshot = getArray();
        if (start >= 0 && end <= snapshot.length && start < end) {
            Object[] init = new Object[end - start];
            System.arraycopy(snapshot, start, init, 0, end - start);
            return new MuggleCopyOnWirteArrayList<T>(init);
        }
        return null;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[] snapshot = getArray();
        return (T[])Arrays.copyOf(snapshot, snapshot.length);
    }

    @Override
    public void clear() {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            setArray(new Object[0]);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new CowIterator();
    }

    @Override
    public int indexOf(T t) {
        Object[] snipshot = getArray();
        return indexOf(snipshot, t);
    }

    int indexOf(Object[] snipshot, T t) {
        int index = -1;
        if (t == null) {
            for (int i = 0; i < snipshot.length; i++) {
                if (snipshot[i] == null) {
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < snipshot.length; i++) {
                if (t.equals(snipshot[i])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    @Override
    public int lastIndexOf(T t) {
        Object[] snipshot = getArray();
        int index = -1;
        if (t == null) {
            for (int i = snipshot.length - 1; i >= 0; i--) {
                if (snipshot[i] == null) {
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = snipshot.length - 1; i >= 0; i--) {
                if (t.equals(snipshot[i])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    @Override
    public boolean add(T t) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            final int index = size();
            checkIndexForAdd(index);
            Object[] newArray = Arrays.copyOf(array, index + 1);
            newArray[index] = t;
            setArray(newArray);
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean addAll(MuggleCollection<T> collection) {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] items = collection.toArray();
            int index = size();
            if (index + items.length < 0) throw new RuntimeException("invalid new array size");
            Object[] newArray = new Object[index + items.length];
            System.arraycopy(array, 0, newArray, 0, index);
            System.arraycopy(items, 0, newArray, index, items.length);
            setArray(newArray);
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean remove(T t) {
        MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] newArray = new Object[size()];
            int count = 0;
            final int size = size();
            if (t == null) {
                for (int i = 0; i < size; i++) {
                    T value = get(i);
                    if (get(i) != null)
                        newArray[count++] = value;
                }
            } else {
                for (int i = 0; i < size; i++) {
                    T value = get(i);
                    if (t.equals(get(i)))
                        newArray[count++] = value;
                }
            }
            newArray = Arrays.copyOf(newArray, count);
            setArray(newArray);
            return size != size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(MuggleCollection<T> collection) {
        MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] newArray = new Object[size()];
            int count = 0;
            final int size = size();
            for (int i = 0; i < size; i++) {
                if (!collection.contains(get(i))) newArray[count++] = get(i);
            }
            newArray = Arrays.copyOf(newArray, count);
            setArray(newArray);
            return size != size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(T t) {
        return indexOf(t) != -1;
    }

    @Override
    public boolean containsAll(MuggleCollection<T> collection) {
        Object[] snipshot = getArray();
        for (T t: collection) {
            if (indexOf(snipshot, t) < 0) return false;
        }
        return false;
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

    class CowIterator implements Iterator<T> {
        Object[] snapshot;
        int cursor;

        CowIterator() {
            snapshot = getArray();
        }
        @Override
        public boolean hasNext() {
            return cursor < snapshot.length;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            return (T)snapshot[cursor++];
        }

        @Override
        public void remove() {
            throw new RuntimeException("unsupport operation");
        }
    }
}
