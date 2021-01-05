package async.demo.completablefuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.LongStream;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author luoruihua
 * @date 2021/01/05
 */
public class RemoteService {
    public static final RemoteService WORKER = new RemoteService();
    public static final ConcurrentMap<Long, Entity> REPOSITORY = new ConcurrentHashMap<>();
    public static final String CONTENT_REPOSITORY = "ABCDEFGHIJKLMNOPQ";
    public static final ConcurrentMap<Long, String> MESSAGE_MAP = new ConcurrentHashMap<>();

    static {
        LongStream.range(1, 101).forEach(id ->
        {
            REPOSITORY.put(id, new Entity(id, CONTENT_REPOSITORY.charAt(RandomUtils.nextInt(0, 17)) + ""));
        });

    }

    private Entity getEntity(final Long id) {
        try {
            Thread.sleep(1000);
            return REPOSITORY.getOrDefault(id, null);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int updateEntity(final Entity entity) {
        try {
            Thread.sleep(100);
            return null != REPOSITORY.put(entity.getId(), entity) ? 1 : 0;
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void reportFinish(final Long transactionId, final Throwable t) {
        MESSAGE_MAP.put(transactionId, null != t ? "EXCEPTION" : "OK");
    }

    private void batchUpdate(final List<Entity> entityList, final Long transactionId) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        CompletableFuture.allOf(entityList.stream().map(entity ->
                CompletableFuture.runAsync(() -> this.updateEntity(entity))).toArray(CompletableFuture[]::new))
                .whenComplete((v, t) -> this.reportFinish(transactionId, t));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("before:" + REPOSITORY);
        final List<Entity> entityList = new ArrayList<>(100);
        LongStream.range(1, 101).forEach(id -> entityList.add(new Entity(id, (CONTENT_REPOSITORY.charAt(RandomUtils.nextInt(0, 17)) + "").toLowerCase())));
        final Long transactionId = 1L;
        WORKER.batchUpdate(entityList, transactionId);
        while (null == MESSAGE_MAP.get(transactionId))  {
            Thread.sleep(100);
        }
        System.out.println("----" +
                "after:" + REPOSITORY);
    }
}