package async.demo.forkjoin.recursivetask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import async.demo.forkjoin.Utils;

/**
 * @author luoruihua
 * @date 2021/01/04
 */
public final class LongSumMain {
	static long calcSum;

	private LongSumMain() {}

	static long seqSum(final int[] array) {
		long sum = 0;
		for (final int value : array) {
			sum += value;
		}
		return sum;
	}

	public static void main(final String[] args) throws Exception {
		final int[] array = Utils.buildRandomIntArray(20000000);
		calcSum = seqSum(array);
		System.out.println("seq sum=" + calcSum);

		final LongSum ls = new LongSum(array, 0, array.length);

		final ForkJoinPool fjp  = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		final ForkJoinTask<Long> result = fjp.submit(ls);
		System.out.println("forkJoin sum= " + result.get());

		fjp.shutdown();

	}
}