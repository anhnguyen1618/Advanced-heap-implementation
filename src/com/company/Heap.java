package com.company;

public interface Heap<A> {
    boolean isEmpty();
    void insert(A key);
    A extractMin();
    A peekMin();
    void decreaseKey(A key, A newKey);
    Heap<A> meld(Heap<A> other);
    void delete(A key);

}
