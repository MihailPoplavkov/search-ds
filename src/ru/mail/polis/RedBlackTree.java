package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    class Node {
        E value;
        Node p;
        Node left;
        Node right;
        boolean isRed;

        Node(E value, boolean isRed, Node p) {
            this.value = value;
            this.isRed = isRed;
            this.p = p;
            this.left = nil;
            this.right = nil;
        }

        @Override
        public String toString() {
            if (value == null) {
                return "";
            }
            final StringBuilder sb = new StringBuilder("N{");
            sb.append(isRed ? "red" : "blk");
            sb.append(",d=").append(value);
            if (left.value != null) {
                sb.append(", l=").append(left);
            }
            if (right.value != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private Node nil = new Node(null, false, null);
    private Node root = nil;
    private int size;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty");
        }
        Node curr = root;
        while (curr.left.value != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty");
        }
        Node curr = root;
        while (curr.right.value != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        ArrayList<E> res = new ArrayList<E>(size);
        inorderTraverse(root, res);
        return res;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr.value == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("expected not null value");
        }
        Node curr = root;
        while (curr != null) {
            int cmp = compare(curr.value, value);
            if (cmp == 0) {
                return true;
            } else if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        Node y = nil;
        Node x = root;
        while (x != nil) {
            y = x;
            int cmp = compare(x.value, value);
            if (cmp > 0) {
                x = x.left;
            } else if (cmp < 0) {
                x = x.right;
            } else {
                return false;
            }
        }
        Node z = new Node(value, true, y);
        if (y == nil) {
            root = z;
        } else {
            int cmp = compare(y.value, value);
            if (cmp > 0) {
                y.left = z;
            } else {
                y.right = z;
            }
        }
        insertFixup(z);
        size++;
        return true;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        Node z = root;
        int cmp;
        while ((cmp = compare(z.value, value)) != 0) {
            if (cmp > 0) {
                z = z.left;
            } else {
                z = z.right;
            }
            if (z == nil) {
                return false;
            }
        }
        Node y = z;
        Node x;
        if (z.left != nil && z.right != nil) {
            y = z.right;
            while (y.left != nil) {
                y = y.left;
            }
        }
        if (y.left != nil) {
            x = y.left;
        } else {
            x = y.right;
        }
        x.p = y.p;
        if (y.p == nil) {
            root = x;
        } else {
            if (y == y.p.left) {
                y.p.left = x;
            } else {
                y.p.right = x;
            }
        }
        if (y != z) {
            z.value = y.value;
        }
        if (!y.isRed) {
            deleteFixup(x);
        }
        size--;
        return true;
    }

    private void rotateLeft(Node a) {
        Node b = a.right;
        a.right = b.left;
        if (b.left != nil) {
            b.left.p = a;
        }
        b.p = a.p;
        if (a.p == nil) {
            root = b;
        } else {
            if (a == a.p.left) {
                a.p.left = b;
            } else {
                a.p.right = b;
            }
        }
        b.left = a;
        a.p = b;
    }

    private void rotateRight(Node a) {
        Node b = a.left;
        a.left = b.right;
        if (b.right != nil) {
            b.right.p = a;
        }
        b.p = a.p;
        if (a.p == nil) {
            root = b;
        } else {
            if (a == a.p.left) {
                a.p.left = b;
            } else {
                a.p.right = b;
            }
        }
        b.right = a;
        a.p = b;
    }

    private void insertFixup(Node z) {
        while (z.p.isRed) {
            if (z.p == z.p.p.left) {
                Node y = z.p.p.right;
                //the first case
                if (y.isRed) {
                    z.p.isRed = false;
                    y.isRed = false;
                    z = z.p.p;
                    z.isRed = true;
                } else {
                    //the second case
                    if (z == z.p.right) {
                        z = z.p;
                        rotateLeft(z);
                    }
                    //the third case
                    z.p.isRed = false;
                    z.p.p.isRed = true;
                    rotateRight(z.p.p);
                }
            }
            else {
                Node y = z.p.p.left;
                //the first case
                if (y.isRed) {
                    z.p.isRed = false;
                    y.isRed = false;
                    z = z.p.p;
                    z.isRed = true;
                } else {
                    //the second case
                    if (z == z.p.left) {
                        z = z.p;
                        rotateRight(z);
                    }
                    //the third case
                    z.p.isRed = false;
                    z.p.p.isRed = true;
                    rotateLeft(z.p.p);
                }
            }
        }
        root.isRed = false;
    }

    private void deleteFixup(Node x) {
        while ((x != root) && (!x.isRed)) {
            if (x == x.p.left) {
                Node w = x.p.right;
                if (w.isRed) {
                    w.isRed = false;
                    x.p.isRed = true;
                    rotateLeft(x.p);
                    w = x.p.right;
                }
                if ((!w.left.isRed) && (!w.right.isRed)) {
                    w.isRed = true;
                    x = x.p;
                } else {
                    if (!w.right.isRed) {
                        w.left.isRed = false;
                        w.isRed = true;
                        rotateLeft(w);
                        w = x.p.right;
                    }
                    w.isRed = x.p.isRed;
                    x.p.isRed = false;
                    w.right.isRed = false;
                    rotateLeft(x.p);
                    x = root;
                }
            } else {
                Node w = x.p.left;
                if (w.isRed) {
                    w.isRed = false;
                    x.p.isRed = true;
                    rotateRight(x.p);
                    w = x.p.left;
                }
                if ((!w.left.isRed) && (!w.right.isRed)) {
                    w.isRed = true;
                    x = x.p;
                } else {
                    if (!w.left.isRed) {
                        w.right.isRed = false;
                        w.isRed = true;
                        rotateRight(w);
                        w = x.p.left;
                    }
                    w.isRed = x.p.isRed;
                    x.p.isRed = false;
                    w.left.isRed = false;
                    rotateRight(x.p);
                    x = root;
                }
            }
        }
        x.isRed = false;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "RBT{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        for (int i = 0; i < 10; i++) {
            System.out.print(tree.add(i / 2) ? "T" : "F");
        }
        System.out.println("\n" + tree);
        System.out.println(tree.size());
        for (int i = 10; i >= 0; i--) {
            System.out.print(tree.remove(i) ? "T" : "F");
        }
        System.out.println("\n" + tree);
        System.out.println(tree.size());

        System.out.println("------------");
        Random rnd = new Random();
        tree = new RedBlackTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 10; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size());
        System.out.println(tree.first());
        System.out.println(tree.last());
    }
}
