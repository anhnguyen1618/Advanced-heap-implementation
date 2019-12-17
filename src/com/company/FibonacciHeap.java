package com.company;

import java.util.*;

public class FibonacciHeap<A extends Comparable<A>> {

    private static final int INIT_DEGREE = 0;

    private Map<Integer, Map<Node, Node>> roots = new HashMap<>();

    private Index index = new Index<A>();

    private Node smallest;

    public Index getIndex() {
        return this.index;
    }

    private Node add(A value, int degree) {
        roots.putIfAbsent(degree, new HashMap<>());
        Node node = new Node(value, null);
        roots.get(degree).put(node, node);

        if (smallest != null) {
            smallest = node.lessThan(smallest) ? node : smallest;
        } else {
            smallest = node;
        }

        index.addIndex(value, node);

        return node;
    }

    public void add(A value) {
        this.add(value, INIT_DEGREE);
    }

    public boolean isEmpty() {
        return smallest == null;
    }

    public  Map<Integer, Map<Node, Node>> getRoots() {
        return this.roots;
    }

    public A peekMin() {
        if (this.smallest == null) return null;

        return this.smallest.getValue();
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
                    throw new Error("hihihi  "+ first.getDegree() + " " + second.getDegree());
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


    public void decreaseKey(A oldKey, A newKey) {
        if (oldKey.compareTo(newKey) < 0) {
            throw new Error("New key { "+ newKey + " } must be smaller than old key { "+ oldKey+ " }");
        }

        Node nodeToUpdate = this.find(oldKey);
        if (nodeToUpdate == null) {
            throw new Error("key not found");
        }


        Node parent = nodeToUpdate.parent;
        //System.out.println(newKey.compareTo(parent.value) + " " + newKey + parent.value);
        if (parent == null || newKey.compareTo(parent.value) >= 0) {
            this.index.removeIndex(nodeToUpdate);
            nodeToUpdate.value = newKey;
            this.index.addIndex(newKey, nodeToUpdate);

            if (parent == null) {
                smallest = newKey.compareTo(smallest.value) < 0 ? nodeToUpdate : smallest;
            }
            return;
        }

        Node topLevelNode =this.add(newKey, nodeToUpdate.getDegree());
        nodeToUpdate.deleted = true;
        topLevelNode.setChildren(nodeToUpdate.children.keySet());
        if (!parent.isRoot()) parent.lostChildrenCount++;
        this.updateParent(nodeToUpdate);

        if (topLevelNode.getDegree() != nodeToUpdate.getDegree()) {
            throw new Error("hihi");
        }

        detachFromParent(parent);
        
    }

    private Node find(A oldKey) {
        Map<Node, Node> inv = this.index.get(oldKey);
        if (inv == null) {
            throw new Error("key not found");
        }

        Node found = inv.values().iterator().next();

        if (found == null) {
            throw new Error("key not found");
        }

        return found;
    }

    private void detachFromParent(Node detachedNode) {

        if (detachedNode.isRoot()) {
            detachedNode.lostChildrenCount = 0;
            return;
        }

        if (detachedNode.lostChildrenCount < 2) {
            return;
        }

        Node parent = detachedNode.parent;

        Node topLevelNode =this.add(detachedNode.value, detachedNode.getDegree());
        detachedNode.deleted = true;
        topLevelNode.setChildren(detachedNode.children.keySet());

        if (!parent.isRoot()) {
            parent.lostChildrenCount++;
            detachFromParent(parent);
        }

        this.updateParent(detachedNode);

    }

    private void updateParent(Node removedNode) {

        Node parent = removedNode.parent;
        int oldDegree = parent.getDegree();

        if (parent.children.get(removedNode) == null) {
            throw new Error("Child not found");
        }

        parent.children.remove(removedNode);

        this.index.removeIndex(removedNode);



        if (removedNode.parent.isRoot()) {
            int newDegree = parent.getDegree();
            this.roots.putIfAbsent(newDegree, new HashMap<>());
            Map<Node, Node> newDegreeGroup = this.roots.get(newDegree);
            newDegreeGroup.put(parent, parent);
            this.roots.get(oldDegree).remove(parent);
        }

    }

    public FibonacciHeap<A> merge(FibonacciHeap<A> heap2) {
        Map<Integer, Map<Node, Node>> otherRoot = heap2.getRoots();

        for (int degree: otherRoot.keySet()) {
            this.roots.putIfAbsent(degree, new HashMap<>());
            Map<Node, Node> degreeGroup = this.roots.get(degree);
            for (Node entry: otherRoot.get(degree).keySet()) {
                degreeGroup.put(entry, entry);
            }
        }

        this.index.mergeIndex(heap2.getIndex());

        if (this.smallest == null) {
            this.smallest = heap2.smallest;
        } else if (heap2.smallest != null) {
            this.smallest = this.smallest.lessThan(heap2.smallest) ? this.smallest : heap2.smallest;
        }

        return this;
    }



    public class Node {
        public boolean deleted;
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

        public void setChildren(Set<Node> children) {
            for (Node child: children) {
                child.parent = this;
                this.children.put(child, child);
            }

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
