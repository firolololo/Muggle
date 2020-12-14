package com.stellar.muggle.invoke;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/14 15:35
 */
public class MuggleQueueInvoke {
    public static void main(String[] args) {
        AtomicInteger count = new AtomicInteger();
        System.out.println(count.get());
    }
}
