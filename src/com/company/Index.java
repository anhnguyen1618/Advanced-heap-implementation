package com.company;

import java.util.HashMap;
import java.util.Map;

public class Index<A, V extends AbstractNode<A>> {
    private Map<A, Map<V, V>> index = new HashMap<>();

    public void addIndex(V node) {

        A key = node.getValue();
        this.index.putIfAbsent(key, new HashMap<>());

        this.index.get(key).put(node, node);
    }

    public Map<A, Map<V, V>> getIndex() {
        return this.index;
    }

    public void removeIndex(V node) {
        Map<V, V> inv = this.index.get(node.getValue());

        if (inv.get(node) == null) {
            throw new Error("cannot be deleted");
        }
        inv.remove(node);

        if (inv.get(node) != null) {
            throw new Error("cannot be deleted");
        }
    }

    public V get(A value) {
        Map<V, V> inv = this.index.get(value);
        if (inv == null) return null;

        return inv.values().iterator().next();
    }

    public void mergeIndex(Index otherIndex) {
        Map<A, Map<V, V>> otherEntry = otherIndex.getIndex();
        for (A key: otherEntry.keySet()) {
            Map<V, V> inv = this.index.get(key);
            if (inv == null) {
                this.index.putIfAbsent(key, otherEntry.get(key));
                continue;
            }

            for (V node: otherEntry.get(key).values()) {
                inv.put(node, node);
            }
        }
    }
}
