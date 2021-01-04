package async.demo.forkjoin.recursivetask;

import java.util.concurrent.RecursiveTask;

class LongSum extends RecursiveTask<Long> {
    static final int SEQUENTIAL_THRESHOLD = 1000;
    private static final long serialVersionUID = -1420082362574319314L;
    int low;
    int high;
    int[] array;

    LongSum(final int[] arr, final int lo, final int hi) {
        this.array = arr;
        this.low = lo;
        this.high = hi;
    }

    @Override
    protected Long compute() {

        if (this.high - this.low <= SEQUENTIAL_THRESHOLD) {

            long sum = 0;
            for (int i = this.low; i < this.high; ++i) {
                sum += this.array[i];
            }
            return sum;
        } else {
            final int mid = this.low + (this.high - this.low) / 2;
            final LongSum left = new LongSum(this.array, this.low, mid);
            final LongSum right = new LongSum(this.array, mid, this.high);
            left.fork();
            final long rightAns = right.compute();
            final long leftAns = left.join();
            return leftAns + rightAns;
        }
    }
}

       