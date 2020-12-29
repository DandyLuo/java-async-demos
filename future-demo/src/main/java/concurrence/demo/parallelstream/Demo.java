package concurrence.demo.parallelstream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author luoruihua
 * @date 2020/12/28 14:49
 */
public final class Demo {
    static final Demo WORKER = new Demo();
    private Demo() {}

    /**
     * 实体对象
     */
    @Data
    public class Entity {
        public Long id;
        public Integer price;
    }

    public List<Entity> randomBuildId(final int size) {
        if (size <= 0) {
            return new ArrayList<>();
        }
        return IntStream.range(0, size - 1).mapToObj(i -> {
            final Entity entity = new Entity();
            entity.setId((long) i);
            entity.setPrice(RandomUtils.nextInt(1, 999999));
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 正确示范类
     */
    public static final class Correct {
        private Correct() {}

        /**
         * parallel add to List
         *
         * @param size
         *
         * @return exactly equal to size
         */
        public static int threadSafeAddToList(final int size) {
            final List<Integer> values = new CopyOnWriteArrayList<>();
            IntStream.range(0, size).parallel().forEach(values::add);
            return values.size();
        }

        /**
         * parallel add to List
         *
         * @param values
         *
         * @return exactly equal to values.size
         */
        public static int threadSafeAddToList2(final List<Integer> values) {
            if (CollectionUtils.isEmpty(values)) {
                return 0;
            }
            List<Integer> collect = values.stream().parallel().collect(Collectors.toList());
            return collect.size();
        }

        /**
         * parallel sum price
         *
         * @param entityList
         *
         * @return sum of entity's price
         */
        public static int sum(final List<Entity> entityList) {
            return entityList.stream().parallel().mapToInt(Entity::getPrice).sum();
        }

        /**
         * 模拟CPU密集型作业的并行处理方法
         *
         * @param size
         */
        public static void mockJob(final int size) {
            final long mainStartTime = System.currentTimeMillis();
            //开始并行执行
            IntStream.range(0, size).parallel().forEach(i -> {
                try {
                    //模拟CPU运算时间
                    Thread.sleep(1000);
                    System.err.println("index：" + i + "，" + "currentThread:" + Thread.currentThread().getName());
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            });
            final long mainEndTime = System.currentTimeMillis();
            System.out.println("执行完毕，总共耗时:" + (mainEndTime - mainStartTime));
        }
    }

    /**
     * 错误示范类
     */
    public static final class Incorrect {
        private Incorrect() {}

        /**
         * 错误原因：在parallelStream内使用线程不安全的容器ArrayList
         *
         * @param size
         *
         * @return highly possible not equal to size
         *
         * @throws java.lang.ArrayIndexOutOfBoundsException
         */
        public static int threadUnsafeAdd(final int size) {
            final List<Integer> values = new ArrayList<>();
            IntStream.range(0, size).parallel().forEach(values::add);
            return values.size();
        }
    }

    public static void main(String[] args) {
        final List<Entity> entities = WORKER.randomBuildId(100);
        final long sum = entities.stream().mapToLong(Entity::getPrice).sum();
        System.out.println(sum == Demo.Correct.sum(entities));
    }
}
