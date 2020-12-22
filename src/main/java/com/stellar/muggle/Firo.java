package com.stellar.muggle;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/22 18:51
 */
public class Firo extends Muggle {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }

    public void greet() {
        super.invoke();
    }
}
