package com.stellar.muggle.invoke;

import com.stellar.muggle.tool.MuggleCyclicBarrier;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/11 17:39
 */
public class MuggleCyclicBarrierInvoke {
    public static void main(String[] args) {
        MuggleCyclicBarrier barrier = new MuggleCyclicBarrier(3, () -> System.out.println("finish"));
        Thread t1 = new Thread(() -> {
            try {
                System.out.println(barrier.await());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                System.out.println(barrier.await());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread t3 = new Thread(() -> {
            try {
//                TimeUnit.SECONDS.sleep(5);
                System.out.println(barrier.await());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t3.start();
//        barrier.reset();
    }
}
