package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
            height = 1;
        }

        E value;
        Node left;
        Node right;
        byte height;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    private byte height(Node a) {
        return a == null ? 0 : a.height;
    }

    private byte diff(Node a) {
        return (byte)(height(a.left) - height(a.right));
    }

    private void fixHeight(Node a) {
        byte l = height(a.left);
        byte r = height(a.right);
        a.height = (byte)(1 + (l > r ? l : r));
    }

    private Node rotateRight(Node a) {
        Node b = a.left;
        a.left = b.right;
        b.right = a;
        fixHeight(a);
        fixHeight(b);
        return b;
    }

    private Node rotateLeft(Node a) {
        Node b = a.right;
        a.right = b.left;
        b.left = a;
        fixHeight(a);
        fixHeight(b);
        return b;
    }

    private Node balance(Node a) {
        fixHeight(a);
        if (diff(a) == 2) {
            if (diff(a.left) < 0) {
                a.left = rotateLeft(a.left);
            }
            return rotateRight(a);
        } else if (diff(a) == -2) {
            if (diff(a.right) > 0) {
                a.right = rotateRight(a.right);
            }
            return rotateLeft(a);
        } else {
            return a;
        }
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Set is empty");
        }
        Node curr = root;
        while (curr.left != null) {
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
        while (curr.right != null) {
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
        if (curr == null) {
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
            throw new NullPointerException("expected not null value");
        }
        Node node = insert(root, value);
        if (node == null) {
            return false;
        }
        root = node;
        return true;
    }

    private Node insert(Node a, E value) {
        if (a == null) {
            return new Node(value);
        } else {
            int cmp = compare(a.value, value);
            if (cmp > 0) {
                Node ins = insert(a.left, value);
                if (ins == null) {
                    return null;
                }
                a.left = ins;
            } else if (cmp < 0) {
                Node ins = insert(a.right, value);
                if (ins == null) {
                    return null;
                }
                a.right = ins;
            } else {
                return null;
            }
        }
        return balance(a);
    }

    private Node findMinChild(Node a) {
        return a.left == null ? a : findMinChild(a.left);
    }

    private Node removeMinChild(Node a) {
        if (a.left == null) {
            return a.right;
        }
        a.left = removeMinChild(a.left);
        return balance(a);
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("expected not null value");
        }
        Node node = delete(root, value);
        if (node == null) {
            return false;
        }
        root = node;
        return true;
    }

    private Node delete(Node a, E value) {
        if (a == null) {
            return null;
        }
        int cmp = compare(a.value, value);
        if (cmp > 0) {
            Node rmv = delete(a.left, value);
            if (rmv == null) {
                return null;
            } else {
                a.left = rmv;
            }
        } else if (cmp < 0) {
            Node rmv = delete(a.right, value);
            if (rmv == null) {
                return null;
            } else {
                a.right = rmv;
            }
        } else {
            Node l = a.left;
            Node r = a.right;
            a.value = null;
            if (r == null) {
                return l;
            } else {
                Node min = findMinChild(r);
                min.right = removeMinChild(r);
                min.left = l;
                return balance(min);
            }
        }
        return balance(a);
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public String toString() {
        return "AVLT{" + root + "}";
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        System.out.println(tree.add(10));
        System.out.println(tree.add(5));
        System.out.println(tree.add(15));
        System.out.println(tree.toString());
        System.out.println(tree.add(2));
        System.out.println(tree.add(8));
        System.out.println(tree.toString());
        System.out.println(tree.add(9));
        System.out.println(tree.add(9));
        System.out.println(tree.toString());
        System.out.println(tree.remove(10));
        System.out.println(tree.remove(100));
        System.out.println(tree.toString());

        System.out.println("------------");
        Random rnd = new Random();
        tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }
}
