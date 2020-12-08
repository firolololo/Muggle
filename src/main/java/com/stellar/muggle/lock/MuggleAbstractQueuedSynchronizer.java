package com.stellar.muggle.lock;

import sun.misc.Unsafe;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/8 10:42
 */
public abstract class MuggleAbstractQueuedSynchronizer extends MuggleAbstractOwnableSynchronizer {

    static final class Node {
        static final Node SHARED = new Node();
        static final Node EXCLUSIVE = null;
        static final int CANCELLED = 1;
        static final int SIGNAL = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;

        volatile int waitStatus;
        volatile Node prev;
        volatile Node next;
        volatile Thread thread;
        Node nextWaiter;

        final boolean isShared() { return nextWaiter == SHARED; }

        final Node predecessor() throws NullPointerException {
            if (prev == null) throw new NullPointerException();
            return prev;
        }

        Node() {}

        Node(Thread thread, Node mode) {this.thread = thread; this.nextWaiter = mode;}

        Node(Thread thread, int waitStatus) {this.thread = thread; this.waitStatus = waitStatus;}
    }

    private transient volatile Node head;
    private transient volatile Node tail;
    private volatile int state;

    protected void setState(int state) {this.state = state;}
    protected int getState() {return this.state;}

    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    static final long spinForTimeoutThreshold = 1000L;

    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) {
                if (compareAndSetHead(new Node())) {
                    tail = head;
                } else {
                    node.prev = t;
                    if (compareAndSetTail(t, node)) {
                        t.next = node;
                        return t;
                    }
                }
            }
        }
    }



    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset(MuggleAbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(MuggleAbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(MuggleAbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }
    private boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset,
                expect, update);
    }
    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
