package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        E value;
        Node parent;
        Node left;
        Node right;
        //diff = h(L) - h(R)
        int diff;

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
        if (isEmpty()) {
            root = new Node(value, null);
            size = 1;
            return true;
        }
        Node curr = root;
        while (true) {
            int cmp = compare(curr.value, value);
            if (cmp == 0) {
                return false;
            } else if (cmp > 0) {
                if (curr.left != null) {
                    curr = curr.left;
                } else {
                    curr.left = new Node(value, curr);
                    curr = curr.left;
                    break;
                }
            } else {
                if (curr.right != null) {
                    curr = curr.right;
                } else {
                    curr.right = new Node(value, curr);
                    curr = curr.right;
                    break;
                }
            }
        }
        size++;
        balanceAdd(curr);
        return true;
    }

    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("expected not null value");
        }
        if (isEmpty()) {
            return false;
        }
        Node curr = root;
        int cmp;
        while ((cmp = compare(curr.value, value)) != 0) {
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == null) {
                return false;
            }
        }
        boolean isRightChild = false;
        if ((curr.left != null) && (curr.right != null)) {
            isRightChild = true;
            Node next = curr.right;
            while (next.left != null) {
                next = next.left;
            }
            curr.value = next.value;
            if (next.parent == curr) {
                curr.right = next.right;
            } else {
                next.parent.left = next.right;
            }
            if (next.right != null) {
                next.right.parent = next.parent;
            }
            next = null;
        } else {
            if (curr.left != null) {
                isRightChild = false;
                reLink(curr.parent, curr, curr.left);
            } else {
                isRightChild = true;
                reLink(curr.parent, curr, curr.right);
            }
        }
        balanceRemove(curr.parent, isRightChild);
        size--;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == null) {
            root = child;
        } else if (parent.left == curr) {
            parent.left = child;
        } else if (parent.right == curr) {
            parent.right = child;
        }
        if (child != null) {
            child.parent = parent;
        }
    }

    private void balanceAdd(final Node last) {
        Node curr = last;
        while (true) {
            Node parent = curr.parent;
            if (parent == null) {
                return;
            }
            if (parent.right == curr) {
                parent.diff--;
            } else {
                parent.diff++;
            }
            if (parent.diff == 2) {
                if (curr.diff >= 0) {
                    rotateRightSmall(parent, curr);
                } else {
                    rotateRightLarge(parent, curr, curr.right);
                }
            } else if (parent.diff == -2) {
                if (curr.diff <= 0) {
                    rotateLeftSmall(parent, curr);
                } else {
                    rotateLeftLarge(parent, curr, curr.left);
                }
            }
            if (parent.diff == 0) {
                return;
            }
            curr = parent;
        }
    }

    private void balanceRemove(final Node last, boolean isRightChild) {
        if (last == null) {
            return;
        }
        Node curr;
        Node parent = last;
        if (isRightChild) {
            parent.diff++;
        } else {
            parent.diff--;
        }
        if (parent.diff == 2) {
            curr = parent.left;
            if (curr.diff >= 0) {
                rotateRightSmall(parent, curr);
            } else {
                rotateRightLarge(parent, curr, curr.right);
            }
        } else if (parent.diff == -2) {
            curr = parent.right;
            if (curr.diff <= 0) {
                rotateLeftSmall(parent, curr);
            } else {
                rotateLeftLarge(parent, curr, curr.left);
            }
        }
        if (Math.abs(parent.diff) == 1) {
            return;
        }
        curr = parent;
        while (true) {
            parent = curr.parent;
            if (parent == null) {
                return;
            }
            if (parent.right == curr) {
                parent.diff++;
            } else {
                parent.diff--;
            }

            if (parent.diff == 2) {
                if (curr.diff >= 0) {
                    rotateRightSmall(parent, curr);
                } else {
                    rotateRightLarge(parent, curr, curr.right);
                }
            } else if (parent.diff == -2) {
                if (curr.diff <= 0) {
                    rotateLeftSmall(parent, curr);
                } else {
                    rotateLeftLarge(parent, curr, curr.left);
                }
            }
            if (Math.abs(parent.diff) == 1) {
                return;
            }
            curr = parent;
        }
    }

    private void rotateRightSmall(Node a, Node b) {
        Node boss = a.parent;
        b.parent = boss;
        a.parent = b;
        a.left = b.right;
        if (b.right != null) {
            b.right.parent = a;
        }
        b.right = a;
        if (boss == null) {
            root = b;
        } else if (boss.right == a) {
            boss.right = b;
        } else {
            boss.left = b;
        }
        if (b.diff == 0) {
            a.diff = 1;
            b.diff = -1;
        } else if (b.diff == 1) {
            a.diff = 0;
            b.diff = 0;
        }
    }

    private void rotateLeftSmall(Node a, Node b) {
        Node boss = a.parent;
        b.parent = boss;
        a.parent = b;
        a.right = b.left;
        if (b.left != null) {
            b.left.parent = a;
        }
        b.left = a;
        if (boss == null) {
            root = b;
        } else if (boss.right == a) {
            boss.right = b;
        } else {
            boss.left = b;
        }
        if (b.diff == 0) {
            a.diff = -1;
            b.diff = 1;
        } else if (b.diff == -1) {
            a.diff = 0;
            b.diff = 0;
        }
    }

    private void rotateRightLarge(Node a, Node b, Node c) {
        int cdiff = c.diff;
        rotateLeftSmall(b, c);
        rotateRightSmall(a, c);
        c.diff = 0;
        switch (cdiff) {
            case -1:
                a.diff = 0;
                b.diff = 1;
                break;
            case 0:
                a.diff = 0;
                b.diff = 0;
                break;
            case 1:
                a.diff = -1;
                b.diff = 0;
        }
    }

    private void rotateLeftLarge(Node a, Node b, Node c) {
        int cdiff = c.diff;
        rotateRightSmall(b, c);
        rotateLeftSmall(a, c);
        c.diff = 0;
        switch (cdiff) {
            case -1:
                a.diff = 1;
                b.diff = 0;
                break;
            case 0:
                a.diff = 0;
                b.diff = 0;
                break;
            case 1:
                a.diff = 0;
                b.diff = -1;
        }
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
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.toString());
        tree.add(2);
        tree.add(8);
        System.out.println(tree.toString());
        tree.add(9);
        tree.add(9);
        System.out.println(tree.toString());
        tree.remove(10);
        System.out.println(tree.toString());
        /*tree.remove(1);
        System.out.println(tree.toString());
        System.out.println(Arrays.toString(tree.inorderTraverse().toArray()));*/
    }
}
