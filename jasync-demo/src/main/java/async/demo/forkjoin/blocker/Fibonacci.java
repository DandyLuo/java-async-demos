package async.demo.forkjoin.blocker;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


/**
 * 斐波那契数列
 * 使用ManagedBlocker
 * Fibonacci: Dijkstra's Sum of Squares
 * F2n-1 = Fn-1²+Fn²
 * F2n = (2 * Fn-1 + Fn) * Fn
 * @author luoruihua
 * @date 2021/01/04
 */
public class Fibonacci {
	public BigInteger f(final int n) {
		final Map<Integer, BigInteger> cache = new ConcurrentHashMap<>(4);
		cache.put(0, BigInteger.ZERO);
		cache.put(1, BigInteger.ONE);
		return this.f(n, cache);
	}

	private final BigInteger RESERVED = BigInteger.valueOf(-1000);

	public BigInteger f(final int n, final Map<Integer, BigInteger> cache) {
		BigInteger result = cache.putIfAbsent(n, this.RESERVED);
		if (result == null) {

			final int half = (n + 1) / 2;

			final RecursiveTask<BigInteger> firstTask = new RecursiveTask<BigInteger>() {
				private static final long serialVersionUID = 7410352642841166993L;

				@Override
				protected BigInteger compute() {
					return Fibonacci.this.f(half - 1, cache);
				}
			};
			firstTask.fork();

			final BigInteger f1 = this.f(half, cache);
			final BigInteger f0 = firstTask.join();

			long time = n > 10_000 ? System.currentTimeMillis() : 0;
			try {

				if (n % 2 == 1) {
					result = f0.multiply(f0).add(f1.multiply(f1));
				} else {
					result = f0.shiftLeft(1).add(f1).multiply(f1);
				}
				synchronized (this.RESERVED) {
					cache.put(n, result);
					this.RESERVED.notifyAll();
				}
			} finally {
				time = n > 10_000 ? System.currentTimeMillis() - time : 0;
				if (time > 50) {
					System.out.printf("f(%d) took %d%n", n, time);
				}
			}
		} else if (result.equals(this.RESERVED)) {
			try {
				final ReservedFibonacciBlocker blocker = new ReservedFibonacciBlocker(n, cache);
				ForkJoinPool.managedBlock(blocker);
				result = blocker.result;
			} catch (final InterruptedException e) {
				throw new CancellationException("interrupted");
			}

		}
		return result;
	}

	private class ReservedFibonacciBlocker implements ForkJoinPool.ManagedBlocker {
		private BigInteger result;
		private final int n;
		private final Map<Integer, BigInteger> cache;

		public ReservedFibonacciBlocker(final int n, final Map<Integer, BigInteger> cache) {
			this.n = n;
			this.cache = cache;
		}

		@Override
		public boolean block() throws InterruptedException {
			synchronized (Fibonacci.this.RESERVED) {
				while (!this.isReleasable()) {
					Fibonacci.this.RESERVED.wait();
				}
			}
			return true;
		}

		@Override
		public boolean isReleasable() {
			return !(this.result = this.cache.get(this.n)).equals(Fibonacci.this.RESERVED);
		}
	}
}