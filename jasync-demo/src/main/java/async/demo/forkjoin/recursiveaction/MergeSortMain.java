package async.demo.forkjoin.recursiveaction;


import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import async.demo.forkjoin.Utils;

/**
 * 归并排序
 * @author luoruihua
 * @date 2021/01/04
 */
public class MergeSortMain {

    private final int[] arrayToSort;
    private final int threshold;

    public MergeSortMain(final int[] arrayToSort, final int threshold) {
        this.arrayToSort = arrayToSort;
        this.threshold = threshold;
    }

    public int[] sequentialSort() {
        return sequentialSort(this.arrayToSort, this.threshold);
    }

    public static int[] sequentialSort(final int[] arrayToSort, final int threshold) {
        if (arrayToSort.length < threshold) {
            Arrays.sort(arrayToSort);
            return arrayToSort;
        }

        final int midpoint = arrayToSort.length / 2;

        int[] leftArray = Arrays.copyOfRange(arrayToSort, 0, midpoint);
        int[] rightArray = Arrays.copyOfRange(arrayToSort, midpoint, arrayToSort.length);

        leftArray = sequentialSort(leftArray, threshold);
        rightArray = sequentialSort(rightArray, threshold);

        return merge(leftArray, rightArray);
    }

    public static int[] merge(final int[] leftArray, final int[] rightArray) {
        final int[] mergedArray = new int[leftArray.length + rightArray.length];
        int mergedArrayPos = 0;
        int leftArrayPos = 0;
        int rightArrayPos = 0;
        while (leftArrayPos < leftArray.length && rightArrayPos < rightArray.length) {
            if (leftArray[leftArrayPos] <= rightArray[rightArrayPos]) {
                mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
                leftArrayPos++;
            } else {
                mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
                rightArrayPos++;
            }
            mergedArrayPos++;
        }

        while (leftArrayPos < leftArray.length) {
            mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
            leftArrayPos++;
            mergedArrayPos++;
        }

        while (rightArrayPos < rightArray.length) {
            mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
            rightArrayPos++;
            mergedArrayPos++;
        }

        return mergedArray;
    }

    public static void main(final String[] args) {
        //第一次测试，验证单线程的归并排序算法
        final int[] arrayToSort = Utils.buildRandomIntArray(100);
        final int[] expectedArray = Arrays.copyOf(arrayToSort, arrayToSort.length);
        Arrays.sort(expectedArray);

        final int nofProcessors = Runtime.getRuntime().availableProcessors();

        final MergeSortMain shortestPathServiceSeq = new MergeSortMain(arrayToSort, nofProcessors);
        final int[] sortSingleThreadArray = shortestPathServiceSeq.sequentialSort();

        System.out.println(Arrays.toString(sortSingleThreadArray));
        System.out.println(Arrays.toString(expectedArray));

        //第二次测试，验证使用forkJoin实现的归并排序
        final int[] arrayToSortSingleThread = Utils.buildRandomIntArray(100);
        final int[] arrayToSortMultiThread = Arrays.copyOf(arrayToSortSingleThread, arrayToSortSingleThread.length);

        final MergeSortMain singleSortMerge = new MergeSortMain(arrayToSortSingleThread, nofProcessors);
        final int[] singleThreadArray = singleSortMerge.sequentialSort();

        final MergeSortAction mergeSortAction = new MergeSortAction(arrayToSortMultiThread, nofProcessors);
        final ForkJoinPool forkJoinPool = new ForkJoinPool(nofProcessors);
        forkJoinPool.invoke(mergeSortAction);

        System.out.println(Arrays.toString(singleThreadArray));
        System.out.println(Arrays.toString(mergeSortAction.getSortedArray()));
    }

}