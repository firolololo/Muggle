package com.stellar.muggle;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/22 18:51
 */
public abstract class Muggle {
    public void sayHello() {
        System.out.println("Muggle");
    }

    public void invoke() {
        this.sayHello();
    }
}
