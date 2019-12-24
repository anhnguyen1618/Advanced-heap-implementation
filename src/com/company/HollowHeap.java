package com.company;

import java.util.*;

public class HollowHeap<A extends Comparable<A>> implements Heap<A> {

    HollowHeapNode root;

    private Index index = new Index<A, HollowHeapNode>();

    /**
     * Check if heap is empty
     * @return
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    /**
     * Insert value to heap
     * Create new node and link new node to root
     * @param item value to be inserted
     */
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
    }

    /**
     * Find min value and remove min value from heap
     * @return value of the smallest element
     */
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

            // Loop through all children of the hollowed node
            while (curChild != null) {
                HollowHeapNode next = curChild.nextSibling;

                if (curChild.isHollow && curChild.secondParent == null) {
                    // The hollowedNode is the first and only parent of curChild
                    // => remove curChild and push curChild to stack to process later
                    hollowedRoots.push(curChild);
                } else if (curChild.secondParent == hollowedNode) {
                    // The hollowedNode is the second parent => the curChild node it stilled attached to its parent
                    // It's important that the algorithm stops processing siblings of curChild here
                    // since it's siblings are children of the first parent, not this hollowedNode
                    // This is because the node with decreased key is the last child of the second parent
                    curChild.secondParent = null;
                    break;
                } else if (curChild.secondParent != null) {
                    // Same as above, the hollowed node is the first parent => when the first parent is destroyed,
                    // all siblings of the curChild is unlinked from curChild as well
                    // (Since we already stored HollowHeapNode next = curChild.nextSibling aboved => we will process next in the next iteration).
                    // When the first parent is destroyed, the second parent becomes the first => curChild.secondParent = null;
                    curChild.secondParent = null;
                    curChild.nextSibling = null;
                } else {
                    // If the curChild is not hollowed node, used ranked link to connect all ranked nodes
                    // until it's not possible to do ranked link
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

        // Do unranked link to make sure that root is only one node.
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

    /**
     * Get value of min without removing it from heap
     * @return min value
     */
    public A peekMin() {
        if (this.root == null) return null;

        return this.root.key;
    }

    /**
     * Decrease key of a node in heap
     * @param oldKey oldkey of the node
     * @param newKey newkey of the node
     */
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
            // Only update key and do nothing if the node to be updated is root
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

        // If newNode is loser => update second parent of old node to be new node and update rank of new node.
        oldNode.secondParent = newNode;
        newNode.prependChild(oldNode);
        newNode.rank = Math.max(0, oldNode.rank - 2);
    }

    /**
     * Merge 2 different HollowHeaps
     * @param other other heap
     * @return merged heap
     */
    @Override
    public Heap<A> meld(Heap other) {
        if (other instanceof HollowHeap) {
            return this.merge((HollowHeap) other);
        }

        throw new Error("Can't combine 2 different types of heaps");
    }

    /**
     * Delete a node with specified key
     * @param key key of the node to be deleted
     */
    public void delete(A key) {
        HollowHeapNode node = this.find(key);
        if (node == null) {
            System.out.println("key " + key + "is not found");
            return;
        }

        if (this.root == node) {
            extractMin();
            return;
        }

        node.isHollow = true;
        this.index.removeIndex(node);

    }

    /**
     * Merge 2 heaps including merging indexes
     * @param other other heap
     * @return merged heap
     */
    private HollowHeap<A> merge(HollowHeap<A> other) {
        if (other.root == null) return this;

        if (this.root == null) return other;

        this.index.mergeIndex(other.index);

        this.linkWithRootAndReturnWinner(other.root);
        return this;
    }


    /**
     * Link unranked
     * @param node1 first node
     * @param node2 second node
     * @return winner node
     */
    private HollowHeapNode link(HollowHeapNode node1, HollowHeapNode node2) {
        if (node1.lessThan(node2)) {
            node1.prependChild(node2);
            return node1;
        }

        node2.prependChild(node1);
        return node2;
    }

    /**
     * Link with root and return the winner
     * @param newNode node to link
     * @return new root
     */
    private HollowHeapNode linkWithRootAndReturnWinner(HollowHeapNode newNode) {
        this.root = link(this.root, newNode);
        return this.root;
    }

    /**
     * Find node with the specified key
     * @param oldKey key of the node to find
     * @return found node
     */
    private HollowHeapNode find(A oldKey) {
        HollowHeapNode found = (HollowHeapNode) this.index.get(oldKey);

        if (found == null) {
            throw new Error("key " + oldKey + " not found");
        }

        return found;
    }

    private class HollowHeapNode implements AbstractNode<A> {
        int rank = 0;
        A key;

        // Flag to mark that this node is hollowed (in the paper, they check if node.item = null to check if node is hollowed
        // However, it's easier to use a special flag to check for that
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
