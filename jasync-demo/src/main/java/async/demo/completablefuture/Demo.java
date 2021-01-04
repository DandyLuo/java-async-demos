package async.demo.completablefuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.sun.istack.internal.NotNull;

/**
 * @author luoruihua
 * @date 2021/1/4 10:00
 */
public final class Demo {
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1,
            100, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(100), new ThreadFactory() {
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public Thread newThread(@NotNull final Runnable r) {
            final String name = "demoThread-" + this.atomicInteger.getAndIncrement() + "-thread";
            return new Thread(r, name);
        }
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    private Demo() {}


    /**
     * 模拟耗时工作
     *
     * @param size
     *
     * @return
     */
    public static List<CompletableFuture<Void>> getFutureList(final int size) {
        final List<CompletableFuture<Void>> list = new ArrayList<>(size);
        IntStream.range(0, size).forEach(num ->
                list.add(CompletableFuture.runAsync(() ->
                    System.out.println(Thread.currentThread().getName() + " job done"))));
        return list;
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        //接收参数为Runnable，无返回值
        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " job done"));
        //接收参数为Supplier，有返回值
        CompletableFuture.supplyAsync(() -> Thread.currentThread().getName()).get();
        //组合操作
        //thenRun:接受一个runnable ，无法读取前面的结果
        CompletableFuture.supplyAsync(() -> Thread.currentThread().getName()).thenRun(() -> System.out.println("thenRun执行，无法拿到前置任务的结果"));
        ////thenApply:接收一个function，可以读取前面的结果并返回新的结果
        final CompletableFuture<String> thenApplyFuture = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName()).thenApply(e -> "thenApply添加" + e);
        System.out.println(Thread.currentThread().getName() + thenApplyFuture.get());
        //thenAccept:可以接收一个consumer，能读取到前面的结果
        CompletableFuture.supplyAsync(() -> Thread.currentThread().getName()).thenAccept(e -> System.out.println("then accept 可以收到前置任务的结果为：" + e));
        //whenComplete:可以设置回调函数
        final CompletableFuture<String> whenCompleteFuture = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName());
        //设置回调，t代表异常thrown
        whenCompleteFuture.whenComplete((e, t) -> System.out.println(Thread.currentThread().getName() + "whenComplete " + e));
        //thenCombine:收集前面的和当前的CompletableFuture的返回值作为参数，传递给function
        final CompletableFuture<String> thenCombineFuture = CompletableFuture.supplyAsync(() -> "then").thenCombine(CompletableFuture.supplyAsync(() -> "combine"), (a, b) -> a + " + " + b);
        System.out.println(thenCombineFuture.get());
        //thenCompose:前面的CompletableFuture返回值可以作为下一个CompletableFuture的参数
        final CompletableFuture<String> thenComposeFuture = CompletableFuture.supplyAsync(() -> "then").thenCompose(str -> CompletableFuture.supplyAsync(() -> str + " + compose"));
        System.out.println(thenComposeFuture.get());
        //all of: 当所有CompletableFuture完成时，使用一个新的CompletableFuture接收结果
        final List<CompletableFuture<Void>> allOfList = Demo.getFutureList(50);
        final CompletableFuture<Void> all = CompletableFuture.allOf(allOfList.toArray(new CompletableFuture[0]));
        //这里返回就代表列表中所有的future全返回了
        all.get();
        System.out.println(allOfList.stream().filter(e -> !e.isDone()).count());
        //any of :当任何一个CompletableFuture完成时，使用一个新的CompletableFuture接收结果
        final List<CompletableFuture<Void>> anyOfList = Demo.getFutureList(50);
        final CompletableFuture<Object> any = CompletableFuture.anyOf(anyOfList.toArray(new CompletableFuture[0]));
        any.get();
        System.out.println(anyOfList.stream().filter(e -> !e.isDone()).count());
    }


}
