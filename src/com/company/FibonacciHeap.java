package com.company;

import java.util.*;

public class FibonacciHeap<A extends Comparable<A>> implements Heap<A> {
    private Set<Node> roots = new HashSet<>();

    private Index<A, Node> index = new Index<>();

    private Node smallest;

    /**
     * Check if heap is empty
     * @return
     */
    public boolean isEmpty() {
        return smallest == null;
    }

    /**
     * Insert value to heap
     * @param value value to be inserted
     */
    public void insert(A value) {
        Node node = new Node(value, null);
        this.index.addIndex(node);
        this.pullToRoot(node);
    }

    /**
     * Find min value and remove min value from heap
     * Then merge all mergable roots
     * @return value of the smallest element
     */
    public A extractMin() {
        if (this.isEmpty()) {
            return null;
        }

        Node prevSmallest = smallest;

        // Reset lostChildrenCount of all children of the smallest element since they are now put to the root
        for (Node child: this.smallest.children) {
            child.lostChildrenCount = 0;
        }

        this.extractChildrenAndMergeRoots(smallest);

        this.index.removeIndex(prevSmallest);

        return prevSmallest.getValue();
    }

    /**
     * Get value of min without removing it from heap
     * @return min value
     */
    public A peekMin() {
        if (this.smallest == null) return null;

        return this.smallest.getValue();
    }

    /**
     * Decrease key of a node in heap
     * @param oldKey oldkey of the node
     * @param newKey newkey of the node
     */
    public void decreaseKey(A oldKey, A newKey) {
        if (oldKey.compareTo(newKey) < 0) {
            throw new Error("New key { "+ newKey + " } must be smaller than old key { "+ oldKey+ " }");
        }

        Node nodeToUpdate = this.find(oldKey);
        if (nodeToUpdate == null) {
            throw new Error("key not found");
        }


        Node parent = nodeToUpdate.parent;

        if (parent == null || newKey.compareTo(parent.value) >= 0) {
            this.index.removeIndex(nodeToUpdate);
            nodeToUpdate.value = newKey;
            this.index.addIndex(nodeToUpdate);

            if (parent == null) {
                smallest = newKey.compareTo(smallest.value) < 0 ? nodeToUpdate : smallest;
            }
            return;
        }

        if (!parent.isRoot()) parent.lostChildrenCount++;

        this.index.removeIndex(nodeToUpdate);
        nodeToUpdate.decreaseKey(newKey);
        this.pullToRoot(nodeToUpdate);
        this.index.addIndex(nodeToUpdate);

        detachFromParent(parent);

    }

    /**
     * Merge 2 FibonacciHeap together
     * @param other another heap
     * @return merged heap
     */
    @Override
    public Heap meld(Heap other) {
        if (other instanceof FibonacciHeap) {
            return this.merge((FibonacciHeap<A>) other);
        }

        throw new Error("Can't combine 2 different types of heap");
    }

    /**
     * Delete a node with specified key
     * @param key key of the node to be deleted
     */
    public void delete(A key) {
        Node foundNode = this.find(key);
        if (foundNode == null) {
            System.out.println("Key: "+ key + " is not found");
            return;
        }

        this.decreaseKey(key, this.peekMin());
        this.extractMin();
    }

    /**
     * Pull the node to roots, cut all connection to old parents, reset lostChildrenCount
     * @param node node to be pulled
     */
    private void pullToRoot(Node node) {
        this.removeConnectionWithParent(node);

        roots.add(node);
        node.lostChildrenCount = 0;

        smallest = smallest != null && smallest.lessThan(node) ? smallest : node;
    }

    /**
     * Extract children of the node, put them to the roots and merge all mergable nodes
     * @param removedNode root node to be removed
     */
    private void extractChildrenAndMergeRoots(Node removedNode) {
        for (Node child: removedNode.children) {
            child.parent = null;
            roots.add(child);
        }

        roots.remove(removedNode);

        // merge all roots with the same rank until all root nodes has different degree rank
        compressRootsAndUpdateSmallest();
    }

    /**
     * Merge all root nodes with the same degree using hashMap
     */
    private void compressRootsAndUpdateSmallest() {
        Map<Integer, Node> degreeMapping = new HashMap<>();

        for (Node rootNode: this.roots) {

            Node currentNode = rootNode;

            while(degreeMapping.get(currentNode.getDegree()) != null) {
                int degree = currentNode.getDegree();
                Node sameDegreeNode = degreeMapping.get(degree);
                Node newNode = currentNode.merge(sameDegreeNode);
                degreeMapping.remove(degree);
                currentNode = newNode;
            }

            degreeMapping.put(currentNode.getDegree(), currentNode);
        }

        this.roots = new HashSet<>();

        // update smallest element
        Node minNode = null;
        for (Node root: degreeMapping.values()) {
            this.roots.add(root);

            if (minNode == null || root.lessThan(minNode)) {
                minNode = root;
            }
        }

        this.smallest = minNode;
    }

    /**
     * Find node with specified key from index
     * @param oldKey key to be searched
     * @return Node with the searched key
     */
    private Node find(A oldKey) {
        Node found = this.index.get(oldKey);

        if (found == null) {
            throw new Error("key not found");
        }

        return found;
    }

    /**
     * Cascade delete if node is marked with detachedNode.lostChildrenCount == 2
     * @param detachedNode node to be checked if they should be pull to top
     */
    private void detachFromParent(Node detachedNode) {

        if (detachedNode.isRoot()) {
            return;
        }

        if (detachedNode.lostChildrenCount < 2) {
            return;
        }

        Node parent = detachedNode.parent;

        this.pullToRoot(detachedNode);

        if (!parent.isRoot()) {
            parent.lostChildrenCount++;
            detachFromParent(parent);
        }
    }

    /**
     * Remove parent link to child and vice versa.
     * If parent is root node, update the degree rank of root node in the hashmap
     * @param removedNode node to remove connection with its parent
     */
    private void removeConnectionWithParent(Node removedNode) {
        Node parent = removedNode.parent;

        removedNode.parent = null;
        if (parent == null) {
            return;
        }

        boolean isRemoved = parent.children.remove(removedNode);

        if (!isRemoved) {
            throw new Error("Child not found");
        }
    }

    /**
     * Merge 2 heaps together including merging there indexes
     * @param heap2 another heap
     * @return merged heap
     */
    private FibonacciHeap<A> merge(FibonacciHeap<A> heap2) {
        Set<Node> otherRoot = heap2.roots;

        this.roots.addAll(otherRoot);

        this.index.mergeIndex(heap2.index);

        if (this.smallest == null) {
            this.smallest = heap2.smallest;
        } else if (heap2.smallest != null) {
            this.smallest = this.smallest.lessThan(heap2.smallest) ? this.smallest : heap2.smallest;
        }

        return this;
    }



    private class Node implements AbstractNode<A> {
        A value;
        Node parent;
        int lostChildrenCount = 0;
        Set<Node> children = new HashSet<>();

        public Node(A value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        public boolean isRoot() {
            return this.parent == null;
        }

        public boolean lessThan(Node other) {
            return this.value.compareTo(other.value) < 0;
        }

        public boolean equals(Node other) {
            return this.value.compareTo(other.value) == 0;
        }

        public void decreaseKey(A newKey) {
            this.value = newKey;
            this.lostChildrenCount = 0;
        }

        public Node merge(Node other) {

            if (this.lessThan(other)) {
                this.children.add(other);
                other.parent = this;
                return this;
            }

            other.children.add(this);
            this.parent = other;
            return other;
        }

        public A getValue() {
            return this.value;
        }

        public int getDegree() {
            return this.children.size();
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(value + "(");
            for (Node child: this.children) {
                result.append(child.toString()).append(", ");
            }
            result.append(")");
            return result.toString();
        }
    }
}
