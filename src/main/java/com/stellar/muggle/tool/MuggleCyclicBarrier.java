package com.stellar.muggle.tool;


import com.stellar.muggle.lock.MuggleReentrantLock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 16:44
 */
public class MuggleCyclicBarrier {
    private static class Generation {
        boolean broken = false;
    }

    private final MuggleReentrantLock lock = new MuggleReentrantLock();
    private final Condition trip = lock.newCondition();
    private final int parties;
    private final Runnable barrierCommand;
    private Generation generation = new Generation();

    // 为什么不用volatile
    private int count;

    public MuggleCyclicBarrier(int parties, Runnable barrierAction) {
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }

    public MuggleCyclicBarrier(int parties) {
        this(parties, null);
    }

    private void nextGeneration() {
        trip.signalAll();
        count = parties;
        generation = new Generation();
    }

    private void breakBarrier() {
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }

    private int doWait(boolean timed, long nanos) throws InterruptedException, BrokenBarrierException,
            TimeoutException {
        final MuggleReentrantLock lock = this.lock;
        // 线程达到获取锁
        lock.lock();
        try {
            // 获取generation信息
            final Generation g = generation;
            // 检查当前generation是否有效
            if (g.broken) throw new BrokenBarrierException();
            if (Thread.interrupted()) {
                breakBarrier();
                throw new InterruptedException();
            }
            // 将等待的线程数量减一
            int index = --count;
            // 如果没有等待线程数 到达barrier
            if (index == 0) {
                // 检查用户行为是否发生错误
                boolean runAction = false;
                try {
                    if (barrierCommand != null) barrierCommand.run();
                    runAction = true;
                    // 成功结束当前generation 唤醒阻塞线程
                    nextGeneration();
                    return 0;
                } finally {
                    // 用户行为出错则中断generation
                    if (!runAction) breakBarrier();
                }
            }
            // 还有线程未完成工作
            for (;;) {
                try {
                    if (!timed) {
                        trip.await();
                    } else if (nanos > 0L) {
                        nanos = trip.awaitNanos(nanos);
                    }
                } catch (InterruptedException e) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw e;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }
                if (g.broken) throw new BrokenBarrierException();
                if (g != generation) return index;
                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isBroken() { return generation.broken; }
    public int getParties() { return parties; }

    public void reset() {
        final MuggleReentrantLock lock = this.lock;
        lock.lock();
        try {
            breakBarrier();
            nextGeneration();
        } finally {
            lock.unlock();
        }
    }

    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return doWait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen
        }
    }

    public int await(long timeout, TimeUnit unit)
            throws InterruptedException,
            BrokenBarrierException,
            TimeoutException {
        return doWait(true, unit.toNanos(timeout));
    }
}
