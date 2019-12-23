import com.company.HollowHeap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class HollowHeapTest {
    HollowHeap<Integer> hollowHeap;
    @Before
    public void setUp() {
        this.hollowHeap = null;
        this.hollowHeap = new HollowHeap<>();
    }

    @Test
    public void testAddAndExtract() {
        Random random = new Random();

        int[] array = random.ints(100000, 10,100000).toArray();
        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        Arrays.sort(array);
        int count = 0;
        while (!this.hollowHeap.isEmpty()) {
            int curValue = this.hollowHeap.extractMin();
            Assert.assertEquals(curValue, array[count]);
            count++;
        }

        assertEquals(array.length, count);
    }

    @Test
    public void peekMinWithoutExtracting() {
        Random random = new Random();

        int[] array = random.ints(20, 10,50).toArray();
        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        int min = hollowHeap.peekMin();
        assertEquals(min, (int) hollowHeap.peekMin());
        assertEquals(min, (int) hollowHeap.peekMin());
        assertEquals(min, (int) hollowHeap.peekMin());

        int count = 0;
        while (!this.hollowHeap.isEmpty()) {
            int curValue = this.hollowHeap.extractMin();
            Assert.assertTrue(curValue >= min);
            min = curValue;
            count++;
        }

        Assert.assertEquals(array.length, count);
    }

    @Test
    public void testDecreaseKey() {
        Random random = new Random();

        int size = 50000;
        int upperBound = 100000;
        int numDecreaseKey = 40000;

        int[] array = random.ints(size, 0,upperBound).toArray();

        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        this.hollowHeap.insert(this.hollowHeap.extractMin());

        for (int i = 0; i < numDecreaseKey; i++) {
            Random rand = new Random();
            int index = rand.nextInt(size);
            int oldValue = array[index];
            array[index] -= rand.nextInt(upperBound);
            this.hollowHeap.decreaseKey(oldValue, array[index]);
        }

        Arrays.sort(array);

        int count = 0;
        while (!this.hollowHeap.isEmpty()) {
            int curValue = this.hollowHeap.extractMin();
            //System.out.println(curValue);
            Assert.assertEquals(curValue, array[count]);
            count++;
        }

        Assert.assertEquals(array.length, count);
    }

    @Test
    public void merge() {
        Random random = new Random();

        HollowHeap<Integer> heap1 = new HollowHeap<>();
        HollowHeap<Integer> heap2 = new HollowHeap<>();

        int[] array = random.ints(20000, 10,20000).toArray();

        for (int i = 0 ; i < array.length; i++) {
            if (i % 3 == 0) {
                heap1.insert(array[i]);
            } else {
                heap2.insert(array[i]);
            }
        }

        Arrays.sort(array);

        HollowHeap<Integer> mergedHeap = heap1.merge(heap2);
        int count = 0;
        while (!mergedHeap.isEmpty()) {
            int curValue = mergedHeap.extractMin();
            Assert.assertEquals(curValue, array[count]);
            count++;
        }

        Assert.assertEquals(array.length, count);
    }
}

