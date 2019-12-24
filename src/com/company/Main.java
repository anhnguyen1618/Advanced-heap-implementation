package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        System.out.println("RUNNING SMALL INPUT TESTS AND DISPLAY INPUT/OUTPUTS AND ACTIONS");

        HollowHeapSmallTest.testHollowHeap();
        HollowHeapSmallTest.testHollowHeap2();

        FibonacciHeapSmallTest.testFibHeap();
        FibonacciHeapSmallTest.testFibHeap2();

        // Large test
        System.out.println("RUNNING TESTS WITH LARGE RANDOMLY GENERATED INPUT .....");

        FibbonacciHeapLargeRandomTest.testAddAndExtract100000();
        FibbonacciHeapLargeRandomTest.peekMinWithoutExtracting();
        FibbonacciHeapLargeRandomTest.testDecreaseKey();
        FibbonacciHeapLargeRandomTest.merge();

        HollowHeapLargeRandomTest.testAddAndExtract100000();
        HollowHeapLargeRandomTest.peekMinWithoutExtracting();
        HollowHeapLargeRandomTest.testDecreaseKey();
        HollowHeapLargeRandomTest.merge();

        System.out.println("DONE!");

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
