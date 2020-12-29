package concurrence.demo.service;

/**
 * @author luoruihua
 * @date 2020/12/11 11:17
 */
public class DemoService {
    public static String doingSomethingA() {
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName() + " has done jobA";
    }

    public static String doingSomethingB() {
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName() + " has done jobB";
    }
}
