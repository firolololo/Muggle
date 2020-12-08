package com.stellar.muggle.lock;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/8 10:37
 */
public abstract class MuggleAbstractOwnableSynchronizer implements java.io.Serializable {

    private Thread exclusiveOwnerThread;

    protected Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }

    protected void setExclusiveOwnerThread(Thread exclusiveOwnerThread) {
        this.exclusiveOwnerThread = exclusiveOwnerThread;
    }
}
