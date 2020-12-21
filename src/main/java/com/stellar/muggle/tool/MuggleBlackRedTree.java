package com.stellar.muggle.tool;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/18 13:48
 */
public class MuggleBlackRedTree<T extends Comparable<T>> {
    static class TreeNode<T> {
        T value;
        TreeNode<T> left;
        TreeNode<T> right;
        TreeNode<T> parent;
        boolean red;
        TreeNode(T value) {
            this.value = value;
            red = true;
        }
    }

    private TreeNode<T> root;
    public MuggleBlackRedTree() {}

    public void put(T t) {
        checkNull(t);
        TreeNode<T> x = insert(t);
        if (x != null) balanceInsertion(x);
    }

    public void remove(T t) {
        checkNull(t);
        delete(t);
    }

    public void printTree() {
        this.printTree("", root, false);
    }

    private TreeNode<T> insert(T t) {
        if (root == null) {
            root = new TreeNode<>(t);
            return root;
        } else {
            TreeNode<T> p = root;
            for (;;) {
                T value = p.value;
                if (t.compareTo(value) == 0) return null;
                if (t.compareTo(value) < 0) {
                    if (p.left == null) {
                        TreeNode<T> x = new TreeNode<>(t);
                        p.left = x; x.parent = p;
                        return x;
                    } else {
                        p = p.left;
                    }
                } else {
                    if (p.right == null) {
                        TreeNode<T> x = new TreeNode<>(t);
                        p.right = x; x.parent = p;
                        return x;
                    } else {
                        p = p.right;
                    }
                }
            }
        }
    }

    private void delete(T t) {
        TreeNode<T> x = findIndex(t);
        if (x == null) return;
        TreeNode<T> xp, xl, xr, s, replacement;
        if ((xr = x.right) != null) {
            s = xr;
            while (s.left != null) s = s.left;
            boolean c = s.red; s.red = x.red; x.red = c;
            T v = s.value; s.value = x.value; x.value = v;
            x = s;
        }
        if (x.right != null) {
            replacement = x.right;
        } else if (x.left != null) {
            replacement = x.left;
        } else {
            replacement = x;
        }
        if ((xp = x.parent) == null) {
            root = replacement;
        } else {
            if (x == xp.left) {
                xp.left = replacement;
            } else {
                xp.right = replacement;
            }
        }
        if (!x.red) balanceDeletion(replacement);
        if (x == replacement) {
            if (xp == null) {
                root = null;
            } else {
                if (x == xp.left) {
                    xp.left = null;
                } else {
                    xp.right = null;
                }
            }
            x.parent = x;
        }
    }

    private TreeNode<T> findIndex(T t) {
        if (root == null) return null;
        TreeNode<T> p = root;
        while (p != null) {
            if (t.compareTo(p.value) == 0) return p;
            if (t.compareTo(p.value) < 0) {
                p = p.left;
            } else {
                p = p.right;
            }
        }
        return null;
    }

    private void rotateRight(TreeNode<T> x) {
        TreeNode<T> xp, xl, xlr;
        if ((xl = x.left) == null) return;
        x.left = null;
        xlr = xl.right;
        if ((xp = x.parent) != null) {
            if (x == xp.left) {
                xp.left = xl;
            } else {
                xp.right = xl;
            }
        }
        xl.right = x;
        x.parent = xl;
        xl.parent = xp;
        if (xp == null) root = xl;
        if (xlr != null) x.left = xlr;
    }

    private void rotateLeft(TreeNode<T> x) {
        TreeNode<T> xp, xr, xrl;
        if ((xr = x.right) == null) return;
        x.right = null;
        xrl = xr.left;
        if ((xp = x.parent) != null) {
            if (x == xp.left) {
                xp.left = xr;
            } else {
                xp.right = xr;
            }
        }
        xr.left = x;
        x.parent = xr;
        xr.parent = xp;
        if (xp == null) root = xr;
        if (xrl != null) x.right = xrl;
    }

    private void balanceInsertion(TreeNode<T> x) {
        TreeNode<T> xp, xpp, xppr, xppl;
        for (;;) {
            if ((xp = x.parent) == null) {
                // root
                x.red = false;
                return;
            } else if ((xpp = xp.parent) == null) {
                return;
            } else if (!xp.red) {
                return;
            } else {
                if (xp == xpp.left) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xp.red = xppr.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.right) {
                            rotateLeft(xp);
                            x = x.left;
                            xp = x.parent;
                        }
                        rotateRight(xpp);
                        xp.red = false;
                        xp.left.red = xp.right.red = true;
                    }
                } else {
                    if ((xppl = xpp.left) != null && xppl.red) {
                        xp.red = xppl.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.left) {
                            rotateRight(xp);
                            x = x.right;
                            xp = x.parent;
                        }
                        rotateLeft(xpp);
                        xp.red = false;
                        xp.left.red = xp.right.red = true;
                    }
                }
            }
        }
    }

    private void balanceDeletion(TreeNode<T> x) {
        for (;;) {
            TreeNode<T> xp, s, sl, sr;
            if ((xp = x.parent) == null) {
                x.red = false;
                return;
            } else if(x.red) {
                x.red = false;
                return;
            } else {
                if (x == xp.left) {
                    if ((s = xp.right) != null && s.red) {
                        s.red = false;
                        xp.red = true;
                        rotateLeft(xp);
                        xp = x.parent;
                        s = (xp == null) ? null : xp.right;
                    }
                    if (s == null) {
                        x = xp;
                    } else {
                        sl = s.left; sr = s.right;
                        if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
                            s.red = true;
                            x = xp;
                        } else  {
                            if (sr == null || !sr.red) {
                                sl.red = false;
                                s.red = true;
                                rotateRight(s);
                                s = xp.right;
                                sr = (s == null) ? null : s.right;
                            }
                            if (s != null) {
                                s.red = xp.red;
                                if (sr != null) sr.red = false;
                                xp.red = false;
                                rotateLeft(xp);
                            }
                            x = root;
                        }
                    }
                } else {
                    if ((s = xp.left) != null && s.red) {
                        s.red = false;
                        xp.red = true;
                        rotateRight(xp);
                        xp = x.parent;
                        s = (xp == null) ? null : xp.left;
                    }
                    if (s == null) {
                        x = xp;
                    } else {
                        sl = s.left; sr = s.right;
                        if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
                            s.red = true;
                            x = xp;
                        } else {
                            if (sl == null || !sl.red) {
                                sr.red = false;
                                s.red = true;
                                rotateLeft(s);
                                s = xp.left;
                                sl = (s == null) ? null : s.left;
                            }
                            if (s != null) {
                                s.red = xp.red;
                                if (sl != null) sl.red = false;
                                xp.red = false;
                                rotateRight(xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }
    }

    private void printTree(String prefix, TreeNode<T> node, boolean isLeft) {
        if (node != null) {
            System.out.println(prefix + (isLeft ? "|--" : "\\--") + node.value + "(" + (node.red ? "red" : "black") + ")");
            printTree(prefix + (isLeft ? "|   " : "    "), node.left, true);
            printTree(prefix + (isLeft ? "|   " : "    "), node.right, false);
        }
    }

    private void checkNull(T t) {
        if (t == null) throw new RuntimeException("Tree node can't be null");
    }
}
