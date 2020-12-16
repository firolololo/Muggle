package com.stellar.muggle.invoke;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/14 15:35
 */
public class MuggleQueueInvoke {
    public static void main(String[] args) {
//        AtomicInteger count = new AtomicInteger();
//        System.out.println(count.get());
        Semaphore semaphore = new Semaphore(1);

        ReentrantLock lock = new ReentrantLock();
        try {
//            semaphore.acquire();
//            semaphore.acquire();
//            semaphore.release();
//            semaphore.release();
            lock.lock();
            lock.lock();
            lock.unlock();
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("finish");
    }
}
