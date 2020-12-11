package com.stellar.muggle.collection;

import java.util.Iterator;

/**
 * Created by wanglecheng on 2020/12/5.
 */
public class MuggleLinkedList<T> extends MuggleAbstractList<T> implements MuggleDeque<T> {

    Node<T> head;
    Node<T> tail;
    transient int size = 0;
    public MuggleLinkedList() {};

    public MuggleLinkedList(MuggleCollection<T> collection) {
        super();
        addAll(collection);
    }

    class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;

        Node (Node<T> prev, T item, Node<T> next){
            this.prev = prev;
            this.item = item;
            this.next = next;
        }

    }


    @Override
    public boolean addFirst(T t) {
        linkFirst(t);
        return true;
    }

    @Override
    public boolean addLast(T t) {
        linkLast(t);
        return true;
    }

    @Override
    public boolean offerFirst(T t) {
        return addFirst(t);
    }

    @Override
    public boolean offerLast(T t) {
        return addLast(t);
    }

    @Override
    public T pollFirst() {
        return head == null? null: unlink(head);
    }

    @Override
    public T pollLast() {
        return tail == null? null: unlink(tail);
    }

    @Override
    public T removeFirst() {
        if (head == null) throw new RuntimeException("No element");
        return unlink(head);
    }

    @Override
    public T removeLast() {
        if (tail == null) throw new RuntimeException("No element");
        return unlink(tail);
    }

    @Override
    public T peekFirst() {
        return head == null? null: head.item;
    }

    @Override
    public T peekLast() {
        return tail == null? null: tail.item;
    }

    @Override
    public T elementFirst() {
        if (head == null) throw new RuntimeException("No element");
        return head.item;
    }

    @Override
    public T elementLast() {
        if (tail == null) throw new RuntimeException("No element");
        return tail.item;
    }

    @Override
    public boolean offer(T t) {
        return addLast(t);
    }

    @Override
    public T poll() {
        return pollFirst();
    }

    @Override
    public T pop() {
        return pollLast();
    }

    @Override
    public T remove() {
        return removeFirst();
    }

    @Override
    public T peek() {
        return peekFirst();
    }

    @Override
    public T element() {
        return elementFirst();
    }

    @Override
    public Iterator<T> iterator() {
        return new NodeItr();
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return node(index).item;
    }

    @Override
    public T set(int index, T t) {
        checkIndex(index);
        Node<T> x = node(index);
        T oldValue = x.item;
        x.item = t;
        return oldValue;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        Node<T> x = node(index);
        unlink(x);
        return x.item;
    }

    @Override
    public void add(int index, T t) {
        checkIndexForAdd(index);
        if (index == size) {
            linkBefore(t, null);
        } else {
            checkIndex(index);
            linkBefore(t, node(index));
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public MuggleList<T> subList(int start, int end) {
        checkIndex(start);
        checkIndex(end - 1);
        MuggleLinkedList<T> list = new MuggleLinkedList<>();
        int nums = end - start;
        for (Node<T> x = node(start); nums > 0; nums--) {
            list.addLast(x.item);
            x = x.next;
        }
        return list;
    }

    @Override
    public T[] toArray() {
        Object[] res = new Object[size];
        int i = 0;
        for (Node<T> x = head; x != null; x = x.next) {
            res[i++] = x.item;
        }
        return (T[])res;
    }

    private Node<T> node(int index) {
       if (index < (size >>> 1)) {
           Node<T> x = head;
           for (int i = 0; i < index; i++) {
               x = x.next;
           }
           return x;
       } else {
           Node<T> x = tail;
           for (int i = 0; i < index; i--) {
               x = x.prev;
           }
           return x;
       }
    }

    private void linkFirst(T t) {
        final Node<T> first = head;
        final Node<T> x = new Node<>(null, t, head);
        head = x;
        if (first == null) {
            tail = x;
        } else {
            first.prev = x;
        }
        size++;
        modCount++;
    }

    private void linkLast(T t) {
        final Node<T> last = tail;
        final Node<T> x = new Node<>(tail, t, null);
        tail = x;
        if (last == null) {
            head = x;
        } else {
            last.next = x;
        }
        size++;
        modCount++;
    }

    private void linkBefore(T t, Node<T> succ) {
        if (succ == null) {
            linkLast(t);
        }
        if (succ == head) {
            linkFirst(t);
        }
        Node<T> x = new Node<T>(succ.prev, t, succ);
        succ.prev = x;
    }

    private T unlink(Node<T> x) {
        if (x == head) {
            head = x.next;
            x.next = null;
        }
        if (x == tail) {
            tail = x.prev;
            x.prev = null;
        }
        if (x.prev != null && x.next != null) {
            x.prev.next = x.next;
            x.next.prev = x.prev;
        }
        x.prev = null;
        x.next = null;
        size--;
        modCount++;
        return x.item;
    }

    class NodeItr extends Itr {
        Node<T> cursor = head;
        Node<T> lastRet;

        @Override
        public boolean hasNext() {
            return this.cursor != null;
        }

        @Override
        public T next() {
            checkForComodification();
            final Node<T> cur = cursor;
            if (cur == null) throw new RuntimeException("No element");
            lastRet = cur;
            cursor = cur.next;
            return cur.item;
        }

        @Override
        public void remove() {
            if (lastRet == null) {
                throw new RuntimeException("Illegal remove");
            }
            checkForComodification();
            unlink(lastRet);
            lastRet = null;
            expectedModCount = modCount;
        }
    }

}
