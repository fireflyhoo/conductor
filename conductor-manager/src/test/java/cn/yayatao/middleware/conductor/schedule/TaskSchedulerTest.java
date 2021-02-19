package cn.yayatao.middleware.conductor.schedule;


import java.util.concurrent.atomic.AtomicInteger;

public class TaskSchedulerTest {


    public static void main(String[] args) {
        testSchedule();
    }

    public static void testSchedule() {
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.start();

        System.out.println(new AtomicInteger().get());

        long s = System.currentTimeMillis() + 10000;
        System.out.println(s);
        scheduler.schedule("666", s, new TimerTaskHandler("666") {
            @Override
            protected void execute(String identity) {
                System.out.println(Thread.currentThread().getName() + "_" + getIdentity() + "_" + System.currentTimeMillis());
            }
        });
        scheduler.schedule("667", s, new TimerTaskHandler("667") {
            @Override
            protected void execute(String identity) {
                System.out.println(Thread.currentThread().getName() + "_" + getIdentity() + "_" + System.currentTimeMillis());
            }
        });
        scheduler.schedule("668", s, new TimerTaskHandler("668") {
            @Override
            protected void execute(String identity) {
                System.out.println(Thread.currentThread().getName() + "_" + getIdentity() + "_" + System.currentTimeMillis());
            }
        });

    }
}
