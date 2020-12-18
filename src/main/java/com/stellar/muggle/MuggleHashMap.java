package com.stellar.muggle;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/16 16:12
 */
public class MuggleHashMap<K,V> extends AbstractMap<K,V>
        implements Map<K,V>, Cloneable, Serializable {
    static final int DEFAULT_INIT_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;

    static class Node<K,V> implements Map.Entry<K,V> {
        int hash;
        K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)obj;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    transient Node<K, V>[] table;
    int threshold;
    final float loadFactor;
    transient Set<Map.Entry<K,V>> entrySet;
    transient int size;
    transient int modCount;

    public MuggleHashMap() {
        loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public MuggleHashMap(int initCapacity, float loadFactor) {
        if (initCapacity < 0) throw new RuntimeException("Invalid initCapacity");
        if (loadFactor < 0 || Float.isNaN(loadFactor)) throw new RuntimeException("Invalid loadFactor");
        if (initCapacity > MAXIMUM_CAPACITY) {
            initCapacity = MAXIMUM_CAPACITY;
        }
        this.threshold = tableSizeFor(initCapacity);
        this.loadFactor = loadFactor;
    }

    public MuggleHashMap(int initCapacity) {
        this(initCapacity, DEFAULT_LOAD_FACTOR);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return null;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    public V put(K key, V value) {
        return null;
    }

    final V putVal(int hash, K key, V value) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, i;
        return null;
    }

    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n < 0 ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private Node<K, V>[] resize() {
        final Node<K, V>[] oldTab = table;
        int newCap = 0;
        int newThr = 0;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        if (oldCap > MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        if (oldCap == 0) {
            if (oldThr > 0) newCap = oldThr;
            else {
                newCap = DEFAULT_INIT_CAPACITY;
            }
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                oldCap >= DEFAULT_INIT_CAPACITY) {
            newThr = oldThr << 1;
        }
        threshold = newThr;
        @SuppressWarnings("unchecked")
        Node<K, V>[] newTab = (Node<K, V>[])new Node[newCap];
        if (oldTab != null) {
            for (int i = 0; i < oldTab.length; i++) {
                Node<K, V> e;
                if ((e = oldTab[i]) != null) {
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    } else {
                        Node<K, V> loHead = null, loTail = null;
                        Node<K, V> hiHead = null, hiTail = null;
                        Node<K, V> next;
                        do {
                             next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loHead == null)
                                    loTail = loHead = e;
                                else
                                    loTail = loTail.next = e;
                            } else {
                                if (hiHead == null)
                                    hiTail = hiHead = e;
                                else
                                    hiTail = hiTail.next = e;
                            }
                        } while ((e = next) != null);
                        if (loHead != null) {
                            loTail.next = null;
                            newTab[i] = loHead;
                        }
                        if (hiHead != null) {
                            hiTail.next = null;
                            newTab[i + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
}
