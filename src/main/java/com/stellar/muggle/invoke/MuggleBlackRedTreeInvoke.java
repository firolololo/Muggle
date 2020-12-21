package com.stellar.muggle.invoke;

import com.stellar.muggle.tool.MuggleBlackRedTree;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/21 10:25
 */
public class MuggleBlackRedTreeInvoke {
    public static void main(String[] args) {
        MuggleBlackRedTree<Integer> tree = new MuggleBlackRedTree<>();
        tree.put(5);
        tree.put(8);
        tree.put(7);
        tree.put(9);
        tree.put(12);
        tree.put(6);
        tree.put(16);
        tree.put(1);
        tree.put(3);
        tree.put(4);
        tree.put(2);
        tree.printTree();
        System.out.println("---------------------------");
        tree.remove(5);
        tree.printTree();
    }
}
