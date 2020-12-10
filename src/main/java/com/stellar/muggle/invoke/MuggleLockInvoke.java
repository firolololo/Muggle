package com.stellar.muggle.invoke;

import com.stellar.muggle.lock.MuggleReentrantLock;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/10 15:07
 */
public class MuggleLockInvoke {
    private  static int num = 0;
    private static MuggleReentrantLock lock = new MuggleReentrantLock();
    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            new Thread(() -> {
//                try {
//                    lock.lock();
//                    num += 1;
//                    System.out.println(num);
//                } finally {
//                    lock.unlock();
//                }
//            }).start();
//        }

        int[] test = new int[10];
        test[0] = 1;
        test[1] = 2;
        test[2] = 3;
        test = Arrays.copyOf(test, 3);
        System.out.println(Arrays.toString(test));
    }
}
