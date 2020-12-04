package com.stellar.muggle.list;

import java.util.Arrays;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:44
 */
public class MuggleArrayList<T> extends MuggleAbstractList<T>
        implements MuggleRandomAccess, java.io.Serializable {
    private static final int DEFAULT_CAPACITY = 10;
    // 用于区分数组列表是通过有参构造函数还是无参构造函数初始化
    private static final Object[] EMPTY_ELEMENT_DATA = {};
    private static final Object[] DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA = {};
    transient Object[] elementData;
    private int size;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public MuggleArrayList() {
        this.elementData = DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA;
    }

    public MuggleArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENT_DATA;
        } else {
            throw new RuntimeException("Illegal capacity:" + initialCapacity);
        }
    }

    public MuggleArrayList(MuggleCollection<? extends T> collection) {
        elementData = collection.toArray();
        if ((size = elementData.length) != 0) {
            if (elementData.getClass() != Object[].class) {
                elementData = Arrays.copyOf(elementData, size, Object[].class);
            }
        } else {
            this.elementData = EMPTY_ELEMENT_DATA;
        }
    }



    @Override
    public T get(int index) {
        checkIndex(index);
        return elementData(index);
    }

    @Override
    public T set(int index, T t) {
        checkIndex(index);
        T oldValue = elementData(index);
        elementData[index] = t;
        return oldValue;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        modCount++;
        T oldValue = elementData(index);
        int moveNums = size - index - 1;
        if (moveNums > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, moveNums);
        }
        elementData[--size] = null;
        return oldValue;
    }

    @Override
    public void add(int index, T t) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        elementData[index] = t;
        size++;
    }

    @Override
    public int indexOf(T t) {
        if (t == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (elementData[i].equals(t)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(T t) {
        if (t == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (elementData[i] == null) return i;
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (elementData[i].equals(t)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public MuggleList<T> subList(int start, int end) {
        checkIndex(start);
        checkIndex(end - 1);
        MuggleArrayList<T> list = new MuggleArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            list.add(elementData(i));
        }
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[])Arrays.copyOf(elementData, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(T t) {
        ensureCapacity(size + 1);
        elementData[size++] = t;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public MuggleIterator<T> iterator() {
        return null;
    }

    private void checkIndex(int index) {
        if (index >= size) {
            throw new RuntimeException("Illegal index");
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new RuntimeException("Illegal index");
        }
    }

    @SuppressWarnings("unchecked")
    private T elementData(int index) {
        return (T)elementData[index];
    }

    private void ensureCapacity(int minCapacity) {
        if (elementData == DEFAULT_CAPACITY_EMPTY_ELEMENT_DATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        modCount++;
        if (minCapacity - elementData.length > 0) grow(minCapacity);
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >>> 1);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        if (newCapacity > MAX_ARRAY_SIZE) {
            if (minCapacity < 0) {
                throw new OutOfMemoryError();
            }
            newCapacity = (minCapacity > MAX_ARRAY_SIZE) ?
                    Integer.MAX_VALUE :
                    MAX_ARRAY_SIZE;
        }
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
}
