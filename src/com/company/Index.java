package com.company;

import java.util.HashMap;
import java.util.Map;

public class Index<A> {
    private Map<A, Map<FibonacciHeap.Node, FibonacciHeap.Node>> index = new HashMap<>();

    public void addIndex(A key, FibonacciHeap.Node node) {
        this.index.putIfAbsent(key, new HashMap<>());
        this.index.get(node.value).put(node, node);
    }

    public Map<A, Map<FibonacciHeap.Node, FibonacciHeap.Node>> getIndex() {
        return this.index;
    }

    public void removeIndex(FibonacciHeap.Node node) {
        Map<FibonacciHeap.Node, FibonacciHeap.Node> inv = this.index.get(node.value);

        if (inv.get(node) == null) {
            throw new Error("cannot be deleted");
        }
        inv.remove(node);

        if (inv.get(node) != null) {
            throw new Error("cannot be deleted");
        }
    }

    public Map<FibonacciHeap.Node, FibonacciHeap.Node> get(A value) {
        return this.index.get(value);
    }

    public void mergeIndex(Index otherIndex) {
        Map<A, Map<FibonacciHeap.Node, FibonacciHeap.Node>> otherEntry = otherIndex.getIndex();
        for (A key: otherEntry.keySet()) {
            Map<FibonacciHeap.Node, FibonacciHeap.Node> inv = this.index.get(key);
            if (inv == null) {
                this.index.putIfAbsent(key, otherEntry.get(key));
                continue;
            }

            for (FibonacciHeap.Node node: otherEntry.get(key).values()) {
                inv.put(node, node);
            }
        }
    }
}
