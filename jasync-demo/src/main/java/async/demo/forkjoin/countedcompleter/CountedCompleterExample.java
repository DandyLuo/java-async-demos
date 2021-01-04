package async.demo.forkjoin.countedcompleter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

import async.demo.forkjoin.Utils;

/**
 * 计算阶乘
 * 演示通过继承CountedCompleter实现完成计算后触发onCompletion方法
 * @author luourihua
 * @date 2021/01/04
 */
public final class CountedCompleterExample {

    private CountedCompleterExample() {}

    private static class FactorialTask extends CountedCompleter<Void> {

        private static final int SEQUENTIAL_THRESHOLD = 5;

        private static final long serialVersionUID = 5485038365918296927L;
        private final List<BigInteger> integerList;
        private int numberCalculated;

        private FactorialTask (final CountedCompleter<Void> parent,
                               final List<BigInteger> integerList) {
            super(parent);
            this.integerList = integerList;
        }

        @Override
        public void compute () {
            if (this.integerList.size() <= SEQUENTIAL_THRESHOLD) {
                this.showFactorial();
            } else {
                final int middle = this.integerList.size() / 2;
                final List<BigInteger> rightList = this.integerList.subList(middle,
                        this.integerList.size());
                final List<BigInteger> leftList = this.integerList.subList(0, middle);
                this.addToPendingCount(2);
                final FactorialTask taskRight = new FactorialTask(this, rightList);
                final FactorialTask taskLeft = new FactorialTask(this, leftList);
                taskLeft.fork();
                taskRight.fork();
            }
            this.tryComplete();
        }

        @Override
        public void onCompletion (final CountedCompleter<?> caller) {
            if (caller == this) {
                System.out.printf("completed thread : %s numberCalculated=%s%n", Thread
                                    .currentThread().getName(), this.numberCalculated);
            }
        }

        private void showFactorial () {
            for (final BigInteger i : this.integerList) {
                final BigInteger factorial = Utils.calculateFactorial(i);
                System.out.printf("%s! = %s, thread = %s%n", i, factorial, Thread
                                    .currentThread().getName());
                this.numberCalculated++;
            }
        }
    }

    public static void main (final String[] args) {
        final List<BigInteger> list = new ArrayList<>();
        for (int i = 3; i < 20; i++) {
            list.add(new BigInteger(Integer.toString(i)));
        }
        ForkJoinPool.commonPool().invoke(
                new FactorialTask(null, list));

    }

}