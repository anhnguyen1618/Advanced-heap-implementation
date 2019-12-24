package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
//        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
//
//        fibHeap.insert(4);
//        fibHeap.insert(6);
//        fibHeap.insert(1);
//
//        System.out.println(fibHeap.extractMin());
//
//
//        fibHeap.insert(8);
//        fibHeap.insert(9);
//        fibHeap.insert(99);
//        fibHeap.insert(10);
//        fibHeap.insert(3);
//        fibHeap.insert(1);
//
//        fibHeap.insert(5);
//
//        System.out.println(fibHeap.extractMin());
//        fibHeap.decreaseKey(5, 2);
//
//        fibHeap.decreaseKey(10, 7);
//        fibHeap.decreaseKey(9, 5);
//
//        fibHeap.decreaseKey(4, 1);
//        fibHeap.decreaseKey(8, 4);
//        fibHeap.insert(9);
////
//        while (!fibHeap.isEmpty()) {
//            System.out.println("result " + fibHeap.extractMin());
//        }
        //FibonacciHeap.Node x = ;
        //fibHeap.decreaseKey(3, 1);

        //testDecreaseKey();
        //testHollowHeap();


        //System.out.println("hello world" + x.key);
	// write your code here

        HollowHeapSmallTest.testHollowHeap();
        HollowHeapSmallTest.testHollowHeap2();

        FibonacciHeapSmallTest.testFibHeap();
        FibonacciHeapSmallTest.testFibHeap2();
    }


    public static void testDecreaseKey() {
        Random random = new Random();
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
        int[] array = random.ints(20000, 20,2000).toArray();
        for (int e: array) {
            fibHeap.insert(e);
        }

        fibHeap.insert(fibHeap.extractMin());

        for (int i = 0; i < 3000; i++) {
            Random rand = new Random();
            int index = rand.nextInt(20000);
            int oldValue = array[index];
            array[index] -= rand.nextInt(100000);
            fibHeap.decreaseKey(oldValue, array[index]);
        }

        Arrays.sort(array);

        int count = 0;

        System.out.println("sort array " + Arrays.toString(array));

        while (!fibHeap.isEmpty()) {
            int curValue = fibHeap.extractMin();
            //System.out.println(curValue + " " + array[count]);
            if (curValue != array[count]) {
                System.out.println("this is bulsshit " + curValue + " " + array[count]);
            }
            count++;
        }

        System.out.println(count +" "+ array.length);

    }
}
