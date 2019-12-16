package com.company;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();

        fibHeap.add(4);
        fibHeap.add(6);
        fibHeap.add(1);

        System.out.println(fibHeap.extractMin().value);


        fibHeap.add(8);
        fibHeap.add(9);
        fibHeap.add(99);
        fibHeap.add(10);
        fibHeap.add(3);
        fibHeap.add(1);

        fibHeap.add(5);

        System.out.println(fibHeap.extractMin().value);
        fibHeap.decreaseKey(5, 2);

        fibHeap.decreaseKey(10, 7);
        fibHeap.decreaseKey(9, 5);

        fibHeap.decreaseKey(4, 1);
        fibHeap.decreaseKey(8, 4);
        fibHeap.add(9);
//
        while (!fibHeap.isEmpty()) {
            System.out.println("result " + fibHeap.extractMin().value);
        }
        //FibonacciHeap.Node x = ;
        //fibHeap.decreaseKey(3, 1);



        //System.out.println("hello world" + x.key);
	// write your code here
    }
}
