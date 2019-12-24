package com.company;

import java.util.*;

public class FibonacciHeap<A extends Comparable<A>> implements Heap<A> {

    private static final int INIT_DEGREE = 0;

    private Map<Integer, Map<Node, Node>> roots = new HashMap<>();

    private Index index = new Index<A, Node>();

    private Node smallest;

    public boolean isEmpty() {
        return smallest == null;
    }

    public void insert(A value) {
        Node node = new Node(value, null);
        this.index.addIndex(node);
        this.pullToRoot(node);
    }

    public A extractMin() {
        if (this.isEmpty()) {
            return null;
        }

        Node prevSmallest = smallest;

        for (Node child: this.smallest.children.keySet()) {
            child.lostChildrenCount = 0;
        }

        this.extractAndMerge(smallest);

        this.index.removeIndex(prevSmallest);

        return prevSmallest.getValue();
    }

    public A peekMin() {
        if (this.smallest == null) return null;

        return this.smallest.getValue();
    }

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

    @Override
    public Heap meld(Heap other) {
        if (other instanceof FibonacciHeap) {
            return this.merge((FibonacciHeap<A>) other);
        }

        throw new Error("Can't combine 2 different types of heap");
    }

    public void delete(A key) {
        Node foundNode = this.find(key);
        if (foundNode == null) {
            System.out.println("Key: "+ key + " is not found");
            return;
        }

        this.decreaseKey(key, this.peekMin());
        this.extractMin();
    }

    private void pullToRoot(Node node) {
        int degree = node.getDegree();
        this.removeConnectionWithParent(node);

        roots.putIfAbsent(degree, new HashMap<>());
        roots.get(degree).put(node, node);
        node.lostChildrenCount = 0;

        smallest = smallest != null && smallest.lessThan(node) ? smallest : node;
    }

    public  Map<Integer, Map<Node, Node>> getRoots() {
        return this.roots;
    }


    private void extractAndMerge(Node removedNode) {
        for (Node child: removedNode.children.keySet()) {
            int degree = child.getDegree();
            child.parent = null;
            roots.putIfAbsent(degree, new HashMap<>());
            roots.get(degree).put(child, child);
        }

        roots.get(removedNode.getDegree()).remove(removedNode);

        while(compress()) {}

        this.updateSmallest();
    }

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

        for (int key: emptyKeys) {
            this.roots.remove(key);
        }

        this.smallest = minNode;
    }

    private boolean compress() {
        for (int degree: this.roots.keySet()) {
            Collection<Node> nodes = this.roots.get(degree).values();

            if (nodes.size() > 1) {
                Iterator<Node> iter = nodes.iterator();
                Node first = iter.next();
                Node second = iter.next();

                if (first.getDegree() != second.getDegree() || degree != first.getDegree()) {
                    throw new Error("Degree does not match"+ first.getDegree() + " " + second.getDegree());
                }

                Node newRoot = first.merge(second);
                this.roots.putIfAbsent(degree + 1, new HashMap<>());
                this.roots.get(degree + 1).put(newRoot, newRoot);

                this.roots.get(degree).remove(first);
                this.roots.get(degree).remove(second);
                return true;
            }

        }

        return false;
    }




    private Node find(A oldKey) {
        Node found = (Node) this.index.get(oldKey);

        if (found == null) {
            throw new Error("key not found");
        }

        return found;
    }

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

    private FibonacciHeap<A> merge(FibonacciHeap<A> heap2) {
        Map<Integer, Map<Node, Node>> otherRoot = heap2.getRoots();

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
