package com.company;

import java.util.Arrays;
import java.util.Random;

public class FibbonacciHeapLargeRandomTest {
    public static void testAddAndExtract100000() {
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
        Random random = new Random();
        int size = 100000;
        int upperBound = 100000;
        

        int[] input = random.ints(size, 0,upperBound).toArray();
        for (int e: input) {
            fibHeap.insert(e);
        }

        Arrays.sort(input);
        
        int count = 0;
        while (!fibHeap.isEmpty()) {
            int curValue = fibHeap.extractMin();
            if (curValue != input[count]) {
                throw new Error("Current value ("+ curValue +") does not match expected value: " + input[count]);
            }
            
            count++;
        }
        
        if (input.length != count) {
            throw new Error("heap size does not match input size");
        }

        System.out.println(FibbonacciHeapLargeRandomTest.class.getName() + "/testAddAndExtract100000: Passed");
    }

    public static void peekMinWithoutExtracting() {
        Random random = new Random();
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
        int size = 20;
        int upperBound = 50;

        int[] input = random.ints(size, 0,upperBound).toArray();
        for (int e: input) {
            fibHeap.insert(e);
        }

        int min = fibHeap.peekMin();
        
        if (min != fibHeap.peekMin()) throw new Error("min does not match");
        if (min != fibHeap.peekMin()) throw new Error("min does not match");
        if (min != fibHeap.peekMin()) throw new Error("min does not match");
        if (min != fibHeap.peekMin()) throw new Error("min does not match");

        int count = 0;
        while (!fibHeap.isEmpty()) {
            int curValue = fibHeap.extractMin();
            if (curValue < min) {
                throw new Error("Current value is smaller than previous");
            }
            min = curValue;
            count++;
        }

        if (input.length != count) {
            throw new Error("heap size does not match input size");
        }

        System.out.println(FibbonacciHeapLargeRandomTest.class.getName() + "/peekMinWithoutExtracting: Passed");
    }

    public static void testDecreaseKey() {
        Random random = new Random();
        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();

        int size = 50000;
        int upperBound = 100000;
        int numDecreaseKey = 40000;

        int[] input = random.ints(size, 0,numDecreaseKey).toArray();
        for (int e: input) {
            fibHeap.insert(e);
        }

        fibHeap.insert(fibHeap.extractMin());

        for (int i = 0; i < numDecreaseKey; i++) {
            Random rand = new Random();
            int index = rand.nextInt(size);
            int oldValue = input[index];
            input[index] -= rand.nextInt(upperBound);
            fibHeap.decreaseKey(oldValue, input[index]);
        }

        Arrays.sort(input);

        int count = 0;
        while (!fibHeap.isEmpty()) {
            int curValue = fibHeap.extractMin();
            if (curValue != input[count]) throw new Error("Expected: " + input[count]+ ". Actual: " + curValue);
            count++;
        }

        if (input.length != count) {
            throw new Error("heap size does not match input size");
        }

        System.out.println(FibbonacciHeapLargeRandomTest.class.getName() + "/testDecreaseKey: Passed");
    }

    public static void merge() {
        Random random = new Random();

        FibonacciHeap<Integer> heap1 = new FibonacciHeap<>();
        FibonacciHeap<Integer> heap2 = new FibonacciHeap<>();

        int size = 20000;
        int upperbound = 20000;

        int[] input = random.ints(size, 0,upperbound).toArray();

        for (int i = 0 ; i < input.length; i++) {
            if (i % 3 == 0) {
                heap1.insert(input[i]);
            } else {
                heap2.insert(input[i]);
            }
        }

        Arrays.sort(input);

        FibonacciHeap<Integer> mergedHeap = (FibonacciHeap<Integer>) heap1.meld(heap2);
        int count = 0;
        while (!mergedHeap.isEmpty()) {
            int curValue = mergedHeap.extractMin();
            if (curValue != input[count]) throw new Error("Expected: " + input[count]+ ". Actual: " + curValue);
            count++;
        }

        if (input.length != count) {
            throw new Error("heap size does not match input size");
        }

        System.out.println(FibbonacciHeapLargeRandomTest.class.getName() + "/merge: Passed");
    }
}
