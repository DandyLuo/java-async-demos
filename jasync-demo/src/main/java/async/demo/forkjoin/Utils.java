package async.demo.forkjoin;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;

/**
 * 工具类
 * @author luoruihua
 * @date 2021/1/4 14:09
 */
public final class Utils {
    private Utils() {}

    public static BigInteger calculateFactorial(final BigInteger i) {
        BigInteger temp = BigInteger.ONE;
        BigInteger factorial = BigInteger.ONE;
        while (i.intValue() >= temp.intValue()) {
            factorial = factorial.multiply(temp);
            temp = temp.add(BigInteger.ONE);
        }
        return factorial;

    }

    public static int[] buildRandomIntArray(final int size) {
        if (size <= 0) {
            return new int[]{};
        }
        final int[] result = new int[size];
        IntStream.range(0, size).forEach(index -> result[index]= RandomUtils.nextInt(0,9999999));
        return result;
    }
}
