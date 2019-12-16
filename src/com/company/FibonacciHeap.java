package com.company;

import java.lang.reflect.Array;
import java.util.*;

public class FibonacciHeap<A extends Comparable<A>> {

    public static final int INIT_DEGREE = 0;

    private Map<Integer, Map<Node, Node>> roots = new HashMap<>();

    private Node smallest;


    private Node add(A value, int degree) {

        roots.putIfAbsent(degree, new HashMap<>());
        Node current = new Node(value, null);
        roots.get(degree).put(current, current);

        if (smallest != null) {
            smallest = current.lessThan(smallest) ? current : smallest;
        } else {
            smallest = current;
        }


        return current;
    }

    public Node add(A value) {
        return this.add(value, INIT_DEGREE);
    }

    public boolean isEmpty() {
        return smallest == null;
    }

    public Node extractMin() {
        if (this.isEmpty()) {
            return null;
        }

        Node prevSmallest = smallest;

        for (Node child: this.smallest.children) {
            child.lostChildrenCount = 0;
        }

        this.extractAndMerge(smallest);


        if (this.isEmpty()) {
            this.smallest = null;
        }

        return prevSmallest;
    }

    private void extractAndMerge(Node removedNode) {
        for (Node child: removedNode.children) {
            int degree = child.getDegree();
            roots.putIfAbsent(degree, new HashMap<>());
            roots.get(degree).put(child, child);
        }

        // we know for sure that the removed node (smallest node) is in the roots
        roots.get(removedNode.getDegree()).remove(removedNode);

        while(merge()) {}

        this.updateSmallest();
    }

    private void updateSmallest() {
        Node minNode = null;
        Set<Integer> keys = this.roots.keySet();
        List<Integer> emptyKeys = new ArrayList<>();
        for (int key: keys) {

            Collection<Node> nodes = this.roots.get(key).values();

            if (nodes.size() == 0) {
                emptyKeys.add(key);
                continue;
            }

            for (Node node: nodes) {
                if (minNode == null) {
                    minNode = node;
                } else if (node.lessThan(minNode)) {
                    minNode = node;
                }
            }

        }


        for (int key: emptyKeys) {
            this.roots.remove(key);
        }

        this.smallest = minNode;
    }

    private boolean merge() {
        for (int degree: this.roots.keySet()) {
            Collection<Node> nodes = this.roots.get(degree).values();

            if (nodes.size() > 1) {
                Iterator<Node> iter = nodes.iterator();
                Node first = iter.next();
                Node second = iter.next();

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
        Node nodeToUpdate = this.find(oldKey);
        if (nodeToUpdate == null) {
            throw new Error("key not found");
        }


        Node parent = nodeToUpdate.parent;
        //System.out.println(newKey.compareTo(parent.value) + " " + newKey + parent.value);
        if (parent == null || newKey.compareTo(parent.value) >= 0) {
            nodeToUpdate.value = newKey;
            smallest = newKey.compareTo(smallest.value) < 0 ? nodeToUpdate : smallest;
            return;
        }

        Node topLevelNode =this.add(newKey, nodeToUpdate.getDegree());
        topLevelNode.setChildren(nodeToUpdate.children);

        detachFromParent(nodeToUpdate, false);

        Node rem = this.find(oldKey);
        if(rem != null) {
            System.out.println(rem);
        }
    }

    private Node find(A oldKey) {
        for (Map<Node, Node> mappings: this.roots.values()) {

            for (Node node: mappings.values()) {
                Node found = findHelper(node, oldKey);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    private Node findHelper(Node currentNode, A key) {
        if (currentNode == null) return null;
        if (currentNode.value.compareTo(key) == 0) return currentNode;

        for (Node node: currentNode.children) {
            Node found = findHelper(node, key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public void detachFromParent(Node detachedNode, boolean pullToTop) {
        Node parent = detachedNode.parent;
        if (parent == null) {
            detachedNode.lostChildrenCount = 0;
            return;
        }

        if (pullToTop) {
            Node topLevelNode =this.add(detachedNode.value);
            topLevelNode.setChildren(detachedNode.children);
        }

        if (parent.parent != null) {
            parent.lostChildrenCount++;
        } else {
            this.roots.get(parent.getDegree()).remove(parent);
        }

        parent.setChildren(this.getRemainingSiblings(detachedNode));




        if (parent.lostChildrenCount == 2) {
            detachFromParent(parent, true);
        }

    }

    public ArrayList<Node> getRemainingSiblings(Node removedNode) {
        boolean filtered = false;
        ArrayList<Node> remainingChildren = new ArrayList<>();
        for (Node node: removedNode.parent.children) {
            //System.out.println(node.equals(removedNode)+ " "+ node.value +" " + removedNode.value);
            if (filtered || !node.equals(removedNode)) {
                remainingChildren.add(node);
            } else {
                filtered = true;
            }

        }
        if (removedNode.parent.parent == null) {
            this.roots.putIfAbsent(remainingChildren.size(), new HashMap<>());
            Map<Node, Node> newDegreeGroup = this.roots.get(remainingChildren.size());
            newDegreeGroup.put(removedNode.parent, removedNode.parent);
            newDegreeGroup.put(removedNode.parent, removedNode.parent);
            this.roots.get(remainingChildren.size() + 1).remove(removedNode.parent);
        }

        return remainingChildren;
    }

    public class Node {
        A value;
        Node parent;
        int lostChildrenCount = 0;
        List<Node> children = new ArrayList<>();

        public Node(A value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        public boolean lessThan(Node other) {
            return this.value.compareTo(other.value) < 0;
        }

        public boolean equals(Node other) {
            return this.value.compareTo(other.value) == 0;
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

        public void setChildren(List<Node> children) {
            this.children = children;
            for (Node child: children) {
                child.parent = this;
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
            for (Node child: this.children) {
                result.append(child.toString()).append(", ");
            }
            result.append(")");
            return result.toString();
        }
    }
}
