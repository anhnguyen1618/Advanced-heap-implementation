package com.company;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        fibHeap.add(4, 4);
        fibHeap.add(6, 6);
        fibHeap.add(1, 1);

        System.out.println(fibHeap.extractMin().key);

        fibHeap.add(8, 8);
        fibHeap.add(9, 9);
        fibHeap.add(99, 99);
        fibHeap.add(10, 10);
        fibHeap.add(3, 3);
        fibHeap.add(1, 1);
        System.out.println(fibHeap.extractMin().key);
        fibHeap.add(5, 5);



        fibHeap.add(1, 1);
        System.out.println(fibHeap.extractMin().key);
        fibHeap.decreaseKey(5, 2);

        fibHeap.decreaseKey(10, 7);
        fibHeap.decreaseKey(9, 5);

        fibHeap.decreaseKey(4, 1);
        fibHeap.decreaseKey(8, 4);
        fibHeap.add(9, 9);

        while (!fibHeap.isEmpty()) {
            System.out.println("result " + fibHeap.extractMin().key);
        }
        //FibonacciHeap.Node x = ;
        //fibHeap.decreaseKey(3, 1);



        //System.out.println("hello world" + x.key);
	// write your code here
    }
}
