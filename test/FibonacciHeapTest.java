import com.company.FibonacciHeap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FibonacciHeapTest {
    FibonacciHeap<Integer> fibHeap;
    @Before
    public void setUp() {
        this.fibHeap = new FibonacciHeap<>();
    }

    @Test
    public void testAddAndExtract() {
        Random random = new Random();

        int[] array = random.ints(100000, 10,100000).toArray();
        for (int e: array) {
            this.fibHeap.add(e);
        }

        int min = Integer.MIN_VALUE;
        int count = 0;
        while (!this.fibHeap.isEmpty()) {
            int curValue = this.fibHeap.extractMin();
            Assert.assertTrue(curValue >= min);
            min = curValue;
            count++;
        }

        Assert.assertEquals(array.length, count);
    }

    @Test
    public void peekMinWithoutExtracting() {
        Random random = new Random();

        int[] array = random.ints(20, 10,50).toArray();
        for (int e: array) {
            this.fibHeap.add(e);
        }

        int min = fibHeap.peekMin();
        assertEquals(min, (int) fibHeap.peekMin());

        int count = 0;
        while (!this.fibHeap.isEmpty()) {
            int curValue = this.fibHeap.extractMin();
            Assert.assertTrue(curValue >= min);
            min = curValue;
            count++;
        }

        Assert.assertEquals(array.length, count);
    }

    @Test
    public void testDecreaseKey() {
        Random random = new Random();

        int[] array = random.ints(50, 20,100).toArray();
        for (int e: array) {
            this.fibHeap.add(e);
        }

        for (int i = 0; i < 20; i++) {
            Random rand = new Random();
            int index = rand.nextInt(50);
            int oldValue = array[index];
            array[index] -= rand.nextInt(20);
            this.fibHeap.decreaseKey(oldValue, array[index]);
        }

        Arrays.sort(array);

        int count = 0;
        while (!this.fibHeap.isEmpty()) {
            int curValue = this.fibHeap.extractMin();
            Assert.assertEquals(curValue, array[count]);
            count++;
        }

        Assert.assertEquals(array.length, count);
    }

    @Test
    public void merge() {
        Random random = new Random();

        FibonacciHeap<Integer> heap1 = new FibonacciHeap<>();
        FibonacciHeap<Integer> heap2 = new FibonacciHeap<>();

        int[] array = random.ints(20000, 10,20000).toArray();

        for (int i = 0 ; i < array.length; i++) {
            if (i % 2 == 0) {
                heap1.add(array[i]);
            } else {
                heap2.add(array[i]);
            }
        }

        Arrays.sort(array);

        FibonacciHeap<Integer> mergedHeap = heap1.meld(heap2);
        int count = 0;
        while (!mergedHeap.isEmpty()) {
            int curValue = mergedHeap.extractMin();
            Assert.assertEquals(curValue, array[count]);
            count++;
        }

        Assert.assertEquals(array.length, count);
    }
}
