package async.demo.forkjoin.recursiveaction;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

/**
 * 通过Fork-join的实现类RecursiveAction实现归并排序
 * @author luoruihua
 * @date 2021/01/04
 */
public class MergeSortAction extends RecursiveAction {

	private static final Logger LOGGER = Logger.getLogger(MergeSortAction.class.getName());
	private static final long serialVersionUID = -9188943914331364945L;
	private final int threshold;
	private int[] arrayToSort;

	public MergeSortAction(final int[] arrayToSort, final int threshold) {
		this.arrayToSort = arrayToSort;
		this.threshold = threshold;
	}

	@Override
	protected void compute() {
		if (this.arrayToSort.length <= this.threshold) {
			//sequential sort
			Arrays.sort(this.arrayToSort);
			return;
		}

		//sort half in parallel
		final int midpoint = this.arrayToSort.length / 2;
		final int[] leftArray = Arrays.copyOfRange(this.arrayToSort, 0, midpoint);
		final int[] rightArray = Arrays.copyOfRange(this.arrayToSort, midpoint, this.arrayToSort.length);

		final MergeSortAction left = new MergeSortAction(leftArray, this.threshold);
		final MergeSortAction right = new MergeSortAction(rightArray, this.threshold);

		//invokeAll(left, right);
		left.fork();
		right.fork();

		left.join();
		right.join();
		//sequential merge
		this.arrayToSort = MergeSortMain.merge(left.getSortedArray(), right.getSortedArray());
	}

	public int[] getSortedArray() {
		return this.arrayToSort;
	}

}