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
        this.hollowHeap = new HollowHeap<>();
    }

    @Test
    public void testAddAndExtract() {
        Random random = new Random();

        int[] array = random.ints(100000, 10,100000).toArray();
        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        int min = Integer.MIN_VALUE;
        int count = 0;
        while (!this.hollowHeap.isEmpty()) {
            int curValue = this.hollowHeap.extractMin();
            Assert.assertTrue(curValue >= min);
            min = curValue;
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

        int size = 100000;
        int upperBound = 100000;
        int numDecreaseKey = 40000;

        int[] array = random.ints(size, 0,upperBound).toArray();
//        int[] array = {22, 68, 85, 71, 45, 49, 28, 97, 93, 88, 46, 33, 44, 60, 24, 60, 71, 28, 66, 71, 86, 69, 28, 58, 75, 49, 39, 79, 23, 29, 94, 74, 58, 88, 47, 83, 21, 23, 25, 62, 29, 69, 51, 60, 47, 56, 23, 74, 90, 95, 58, 55, 61, 71, 88, 68, 62, 77, 32, 51, 68, 61, 26, 98, 34, 57, 97, 49, 81, 54, 33, 62, 73, 80, 93, 50, 59, 78, 29, 85, 93, 48, 99, 28, 83, 69, 25, 35, 54, 89, 72, 53, 20, 59, 23, 72, 53, 44, 46, 86};

        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        //this.hollowHeap.insert(this.hollowHeap.extractMin());

        for (int i = 0; i < numDecreaseKey; i++) {
            Random rand = new Random();
            int index = rand.nextInt(size);
            int oldValue = array[index];
            array[index] -= rand.nextInt(upperBound);
            //System.out.println("new value " + oldValue+ " =>>  " + array[index]);
//            System.out.println("array["+index+"] -= 20;");
//            System.out.println("this.hollowHeap.decreaseKey(" + oldValue +", "+ array[index] +");");
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
    public void test() {
        int[] array = {1, 3, 4, 5, 6};
        for (int e: array) {
            this.hollowHeap.insert(e);
        }

        this.hollowHeap.decreaseKey(6, 5);
        //this.hollowHeap.decreaseKey(5, 2);
        System.out.println("--------------------");
        while (!this.hollowHeap.isEmpty()) {
            System.out.println(this.hollowHeap.extractMin());
        }
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

