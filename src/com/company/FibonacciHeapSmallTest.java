package com.company;

import java.util.Arrays;

public class FibonacciHeapSmallTest {
    public static void testFibHeap() {
        System.out.println("========== TEST FIBONACCI HEAP EXTRACT MIN AND DECREASING KEYS IN EXAMPLE ==========");

        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
        int[] input = {14, 11, 5, 9, 0, 8, 10, 3, 6, 12, 13, 4};

        System.out.println("INPUT: " + Arrays.toString(input));

        for (int i : input) {
            fibHeap.insert(i);
        }

        // sorted input
        int[] expectedOutputWithoutDecreasingKey = {0, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14};
        System.out.println("EXPECTED OUTPUT WIHOUT DECEASING KEY (SORTED INPUT): " + Arrays.toString(expectedOutputWithoutDecreasingKey));


        int min = fibHeap.extractMin();
        System.out.println("EXTRACT MIN VALUE: " + min);
        if (min != expectedOutputWithoutDecreasingKey[0]) {
            throw new Error("Min value should be "+ expectedOutputWithoutDecreasingKey[0] + " but it is " + min);
        }

        int[] remainingOutput = {3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14};
        System.out.println("EXPECTED REMAINING OUTPUT AFTER EXTRACTING 0: " + Arrays.toString(remainingOutput));

        System.out.println("--------------------------");

        // Test decreasing keys
        System.out.println("DECREASING KEY: " + 5 + " -> " + 1);
        fibHeap.decreaseKey(5, 1);

        System.out.println("DECREASING KEY: " + 3 + " -> " + 2);
        fibHeap.decreaseKey(3, 2);

        System.out.println("DECREASING KEY: " + 8 + " -> " + 7);
        fibHeap.decreaseKey(8, 7);


        int[] expectedOutputAfterDecreasingKey = {1, 2, 4, 6, 7, 9, 10, 11, 12, 13, 14};
        int[] actualResults = new int[expectedOutputAfterDecreasingKey.length];

        int count = 0;
        while (!fibHeap.isEmpty()) {
            int curMin = fibHeap.extractMin();
            actualResults[count] = curMin;

            int expectedValue = expectedOutputAfterDecreasingKey[count];

            if (curMin != expectedValue) {
                throw new Error("Expected value: " + expectedValue + ", but receives: " + curMin);
            }
            count++;
        }

        if (count != expectedOutputAfterDecreasingKey.length) {
            throw new Error (
                    "Size of hollow heap does not match size of expected output. Expected size: "
                            + expectedOutputAfterDecreasingKey.length
                            +" . Actual size: "+ count);
        }

        System.out.println("OUTPUT AFTER EXTRACTING 0, DECREASING KEYS AND EXTRACTING ALL REMAINING: " + Arrays.toString(actualResults));
        System.out.println("================================= TEST END =================================");
        System.out.println("");
    }

    public static void testFibHeap2() {
        System.out.println("========== TEST FIBONACCI HEAP EXTRACT MIN, DECREASING KEYS, DELETE KEYS IN EXAMPLE 2 ==========");

        FibonacciHeap<Integer> fibHeap = new FibonacciHeap<>();
        int[] input = {0, 8, 1, 11, 4, 15, 3, 16, 13, 6, 7, 9, 17, 20, 10, 18, 12};

        System.out.println("INPUT: " + Arrays.toString(input));

        for (int i : input) {
            fibHeap.insert(i);
        }

        // sorted input
        int[] expectedOutputWithoutDecreasingKey = {0, 1, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 20};
        System.out.println("EXPECTED OUTPUT WIHOUT DECEASING KEY (SORTED INPUT): " + Arrays.toString(expectedOutputWithoutDecreasingKey));


        int min = fibHeap.extractMin();
        System.out.println("EXTRACT MIN VALUE: " + min);
        if (min != expectedOutputWithoutDecreasingKey[0]) {
            throw new Error("Min value should be "+ expectedOutputWithoutDecreasingKey[0] + " but it is " + min);
        }

        int[] remainingOutput = {1, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 20};
        System.out.println("EXPECTED REMAINING OUTPUT AFTER EXTRACTING 0: " + Arrays.toString(remainingOutput));

        System.out.println("--------------------------");

        // Test decreasing keys
        System.out.println("DECREASING KEY: " + 6 + " -> " + 2);
        fibHeap.decreaseKey(6, 2);

        System.out.println("DELETING KEY: " + 4);
        fibHeap.delete(4);

        System.out.println("DELETING KEY: " + 12);
        fibHeap.delete(12);

        int[] expectedOutputAfterDecreasingKey = {1, 2, 3, 7, 8, 9, 10, 11, 13, 15, 16, 17, 18, 20};
        int[] actualResults = new int[expectedOutputAfterDecreasingKey.length];

        int count = 0;
        while (!fibHeap.isEmpty()) {
            int curMin = fibHeap.extractMin();
            actualResults[count] = curMin;

            int expectedValue = expectedOutputAfterDecreasingKey[count];

            if (curMin != expectedValue) {
                throw new Error("Expected value: " + expectedValue + ", but receives: " + curMin);
            }
            count++;
        }

        if (count != expectedOutputAfterDecreasingKey.length) {
            throw new Error (
                    "Size of hollow heap does not match size of expected output. Expected size: "
                            + expectedOutputAfterDecreasingKey.length
                            +" . Actual size: "+ count);
        }

        System.out.println("OUTPUT AFTER EXTRACTING 0, DECREASING KEYS, DELETING KEYS AND EXTRACTING ALL REMAINING: " + Arrays.toString(actualResults));
        System.out.println("================================= TEST END =================================");
        System.out.println("");
    }
}
