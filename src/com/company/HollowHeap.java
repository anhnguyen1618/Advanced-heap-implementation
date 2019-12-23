package com.company;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class HollowHeap<A extends Comparable<A>> {

    HollowHeapNode root;

    private Index index = new Index<A, HollowHeapNode>();

    public HollowHeap merge(HollowHeap other) {
        if (other.root == null) return this;

        if (this.root == null) return other;

        this.index.mergeIndex(other.index);

        this.linkWithRootAndReturnWinner(other.root);
        return this;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(A item) {
        HollowHeapNode newNode = new HollowHeapNode(item);

        this.index.addIndex(newNode);

        if (this.root == null) {
            this.root = newNode;
            return;
        }

        if (this.root.lessThan(newNode)) {
            newNode.nextSibling = this.root.firstChild;
            this.root.firstChild = newNode;

        } else {
            newNode.firstChild = this.root;
            this.root = newNode;
        }
        //System.out.println("run insert");
    }

    public A peekMin() {
        if (this.root == null) return null;

        return this.root.key;
    }

    private HollowHeapNode link(HollowHeapNode node1, HollowHeapNode node2) {
        if (node1.lessThan(node2)) {
            node1.prependChild(node2);
            return node1;
        }

        node2.prependChild(node1);
        return node2;
    }

    private HollowHeapNode linkWithRootAndReturnWinner(HollowHeapNode newNode) {
        this.root = link(this.root, newNode);

        return this.root;
    }

    public void decreaseKey(A oldKey, A newKey) {

        if (oldKey.compareTo(newKey) < 0) {
            throw new Error("Old key has to be bigger than new key");
        }

        HollowHeapNode oldNode = this.find(oldKey);

        if (oldNode == null) {
            throw new Error("Node with key "+ oldKey + "is not found");
        }

        this.index.removeIndex(oldNode);

        if (oldNode == this.root) {
            this.root.key = newKey;
            this.index.addIndex(this.root);
            return;
        }

        oldNode.isHollow = true;
        HollowHeapNode newNode = new HollowHeapNode(newKey);
        this.index.addIndex(newNode);

        HollowHeapNode winner = this.linkWithRootAndReturnWinner(newNode);

        if (winner == newNode) {
            return;
        }


        oldNode.secondParent = newNode;
        newNode.prependChild(oldNode);

        newNode.rank = Math.max(0, oldNode.rank - 2);
    }

    private HollowHeapNode find(A oldKey) {
        HollowHeapNode found = (HollowHeapNode) this.index.get(oldKey);

        if (found == null) {
            throw new Error("key not found");
        }

        return found;
    }

    public A extractMin() {
        if (this.root == null) return null;

        A min = this.root.key;
        this.index.removeIndex(this.root);

        Stack<HollowHeapNode> hollowedRoots = new Stack<>();
        Map<Integer, HollowHeapNode> fullRoots = new HashMap<>();

        this.root.isHollow = true;
        hollowedRoots.push(this.root);

        while (!hollowedRoots.isEmpty()) {
            HollowHeapNode hollowedNode = hollowedRoots.pop();
            HollowHeapNode curChild = hollowedNode.firstChild;


            while (curChild != null) {

                HollowHeapNode next = curChild.nextSibling;

                if (curChild.isHollow && curChild.secondParent == null) {
                    hollowedRoots.push(curChild);
                } else if (curChild.secondParent == hollowedNode) {
                    curChild.secondParent = null;
                    break;
                } else if (curChild.secondParent != null) {
                    curChild.secondParent = null;
                    curChild.nextSibling = null;
                } else {

                    HollowHeapNode currentNode = curChild;
                    currentNode.nextSibling = null;
                    while (fullRoots.get(currentNode.rank) != null) {
                        HollowHeapNode sameRankedNode = fullRoots.get(currentNode.rank);
                        HollowHeapNode winner = this.link(sameRankedNode, currentNode);

                        fullRoots.remove(sameRankedNode.rank);

                        winner.rank++;
                        currentNode = winner;

                    }

                    fullRoots.put(currentNode.rank, currentNode);

                }

                curChild = next;

            }


        }

        HollowHeapNode winner = null;

        for (HollowHeapNode node: fullRoots.values()) {
            if (winner == null) {
                winner = node;
            } else {
                winner = link(winner, node);
            }
        }

        this.root = winner;

        return min;
    }

    private class HollowHeapNode implements AbstractNode<A> {
        int rank = 0;
        A key;
        boolean isHollow = false;
        HollowHeapNode firstChild;
        HollowHeapNode nextSibling;
        HollowHeapNode secondParent;

        public HollowHeapNode(A item) {
            this.key = item;
        }


        public void prependChild(HollowHeapNode newChild) {

            HollowHeapNode curNode = newChild;
            while (curNode.nextSibling != null) {
                curNode = curNode.nextSibling;
            }

            curNode.nextSibling = this.firstChild;

            this.firstChild = newChild;

        }

        public boolean lessThan(HollowHeapNode another) {
            return this.key.compareTo(another.key) < 0;
        }

        @Override
        public A getValue() {
            return this.key;
        }
    }
}
