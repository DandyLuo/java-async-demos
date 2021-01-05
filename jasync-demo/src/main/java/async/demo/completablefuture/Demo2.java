package async.demo.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

public class Demo2 {
    RemoteService remoteService = new RemoteService();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class FrontEntity {
        private Long id;
        private String contentA;
        private String contentB;
    }

    private class RemoteService {
        public final ConcurrentMap<Long, EntityA> repositoryA = new ConcurrentHashMap<>();
        public final ConcurrentMap<Long, EntityB> repositoryB = new ConcurrentHashMap<>();
        public final String contentRepository = "ABCDEFGHIJKLMNOPQ";

        {
            LongStream.range(1, 101).forEach(id ->
            {
                this.repositoryA.put(id, new EntityA(id, this.contentRepository.charAt(RandomUtils.nextInt(0, 17)) + ""));
                this.repositoryB.put(id, new EntityB(id, this.contentRepository.charAt(RandomUtils.nextInt(0, 17)) + ""));
            });

        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class EntityA {
            private Long id;
            private String contentA;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class EntityB {
            private Long id;
            private String contentB;
        }

        private EntityA getEntityA(final Long id) {
            try {
                Thread.sleep(1000);
                return this.repositoryA.getOrDefault(id, null);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private EntityB getEntityB(final Long id) {
            try {
                Thread.sleep(1000);
                return this.repositoryB.getOrDefault(id, null);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private FrontEntity aggregateResult(final Long id) throws ExecutionException, InterruptedException {
        if (null == id) {
            return new FrontEntity();
        }
        final CompletableFuture<FrontEntity> completableFuture = CompletableFuture.supplyAsync(() -> this.remoteService.getEntityA(id))
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> this.remoteService.getEntityB(id)),
                        (a, b) -> new FrontEntity(a.getId(), a.getContentA(), b.getContentB()));
        return completableFuture.get();
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final long start = System.currentTimeMillis();
        System.out.println(new Demo2().aggregateResult(2L));
        final long end = System.currentTimeMillis();
        System.out.printf("耗时:%s", end - start);
    }
}