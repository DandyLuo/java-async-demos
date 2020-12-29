package concurrence.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.istack.internal.NotNull;
import concurrence.demo.service.DemoService;

/**
 * @author luoruihua
 * @date 2020/12/11 11:12
 */
public final class FutureTaskDemo {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(100), new ThreadFactory() {
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public Thread newThread(@NotNull final Runnable r) {
            final String name = "demoThread-" + this.atomicInteger.getAndIncrement() + "-thread";
            return new Thread(r, name);
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    private FutureTaskDemo() {}

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final long start = System.currentTimeMillis();
        //1.创建future子任务
        final FutureTask<String> f1 = new FutureTask<>(DemoService::doingSomethingA);
        THREAD_POOL_EXECUTOR.execute(f1);
        //2.用main线程执行任务B
        final String result2 = DemoService.doingSomethingB();
        //3.阻塞获取future1的执行结果
        final String result1 = f1.get();
        System.out.println(result1 + " " + result2);
        System.out.println(System.currentTimeMillis() - start);
        //4.关闭线程池，否则有用户线程在运行，JVM不会退出
        THREAD_POOL_EXECUTOR.shutdown();
    }
}
