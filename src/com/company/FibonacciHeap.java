package com.company;

import java.util.*;

public class FibonacciHeap<A extends Comparable<A>> implements Heap<A> {
    private Map<Integer, Map<Node, Node>> roots = new HashMap<>();

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
        for (Node child: this.smallest.children.keySet()) {
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
        int degree = node.getDegree();
        this.removeConnectionWithParent(node);

        roots.putIfAbsent(degree, new HashMap<>());
        roots.get(degree).put(node, node);
        node.lostChildrenCount = 0;

        smallest = smallest != null && smallest.lessThan(node) ? smallest : node;
    }

    /**
     * Extract children of the node, put them to the roots and merge all mergable nodes
     * @param removedNode root node to be removed
     */
    private void extractChildrenAndMergeRoots(Node removedNode) {
        for (Node child: removedNode.children.keySet()) {
            int degree = child.getDegree();
            child.parent = null;
            roots.putIfAbsent(degree, new HashMap<>());
            roots.get(degree).put(child, child);
        }

        roots.get(removedNode.getDegree()).remove(removedNode);

        // merge all roots with the same rank until all root nodes has different degree rank
        while(compress()) {}

        this.updateSmallest();
    }

    /**
     * Iterate through roots and select the smallest node
     */
    private void updateSmallest() {
        Node minNode = null;
        Set<Integer> keys = this.roots.keySet();
        List<Integer> emptyKeys = new ArrayList<>();
        for (int key: keys) {

            Collection<Node> roots = this.roots.get(key).values();

            if (roots.size() == 0) {
                emptyKeys.add(key);
                continue;
            }

            for (Node node: roots) {
                if (minNode == null || node.lessThan(minNode)) {
                    minNode = node;
                }
            }

        }

        // remove all rank entries that are empty
        for (int key: emptyKeys) {
            this.roots.remove(key);
        }

        this.smallest = minNode;
    }

    /**
     * Merge all root nodes with the same degree in the hashmap
     * @return status if the merge process should continue
     */
    private boolean compress() {
        for (int degree: this.roots.keySet()) {
            Collection<Node> nodes = this.roots.get(degree).values();

            if (nodes.size() > 1) {
                Iterator<Node> iter = nodes.iterator();
                Node first = iter.next();
                Node second = iter.next();

                if (first.getDegree() != second.getDegree()) {
                    throw new Error("Degree does not match"+ first.getDegree() + " " + second.getDegree());
                }

                Node newRoot = first.merge(second);

                int newDegreeAfterMerging = degree +1;
                // increase rank of the merged node
                this.roots.putIfAbsent(newDegreeAfterMerging, new HashMap<>());
                this.roots.get(newDegreeAfterMerging).put(newRoot, newRoot);

                this.roots.get(degree).remove(first);
                this.roots.get(degree).remove(second);
                return true;
            }

        }

        return false;
    }

    /**
     * Find node with specified key from index
     * @param oldKey key to be searched
     * @return Node with the searched key
     */
    private Node find(A oldKey) {
        Node found = (Node) this.index.get(oldKey);

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

        int oldDegree = parent.getDegree();

        if (parent.children.get(removedNode) == null) {
            throw new Error("Child not found");
        }

        parent.children.remove(removedNode);

        if (parent.isRoot()) {
            int newDegree = parent.getDegree();
            this.roots.putIfAbsent(newDegree, new HashMap<>());
            Map<Node, Node> newDegreeGroup = this.roots.get(newDegree);
            newDegreeGroup.put(parent, parent);
            this.roots.get(oldDegree).remove(parent);
        }

    }

    /**
     * Merge 2 heaps together including merging there indexes
     * @param heap2 another heap
     * @return merged heap
     */
    private FibonacciHeap<A> merge(FibonacciHeap<A> heap2) {
        Map<Integer, Map<Node, Node>> otherRoot = heap2.roots;

        for (int degree: otherRoot.keySet()) {
            this.roots.putIfAbsent(degree, new HashMap<>());
            Map<Node, Node> degreeGroup = this.roots.get(degree);
            for (Node entry: otherRoot.get(degree).keySet()) {
                degreeGroup.put(entry, entry);
            }
        }

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
        Map<Node, Node> children = new HashMap<>();

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
                this.children.put(other, other);
                other.parent = this;
                return this;
            }

            other.children.put(this, this);
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
            for (Node child: this.children.keySet()) {
                result.append(child.toString()).append(", ");
            }
            result.append(")");
            return result.toString();
        }
    }
}
