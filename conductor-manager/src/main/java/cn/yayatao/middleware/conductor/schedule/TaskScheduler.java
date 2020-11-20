package cn.yayatao.middleware.conductor.schedule;

import cn.yayatao.middleware.conductor.common.LifeCycle;
import cn.yayatao.middleware.conductor.schedule.timer.Timer;
import cn.yayatao.middleware.conductor.schedule.timer.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TaskScheduler implements LifeCycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);

    /**
     * 默认最小时间轮刻度
     */
    private static final long DEFAULT_TICK_MS = 1000;
    private static final int DEFAULT_WHEEL_SIZE = 100;


    private final AtomicBoolean started = new AtomicBoolean(false);


    /***
     * 定时器
     */
    private final Timer timer;


    public TaskScheduler() {
        this.timer = new Timer(DEFAULT_TICK_MS, DEFAULT_WHEEL_SIZE);
    }

    @Override
    public void start() {
        if(!started.compareAndSet(false,true)){
            LOGGER.warn("task scheduler has started");
            return;
        }
        timer.start();
    }

    @Override
    public void stop() {
        if (started.compareAndSet(true,false)){
            LOGGER.warn("task scheduler has closed.");
            return;
        }
        timer.stop();
    }

    @Override
    public boolean isRunning() {
        return started.get();
    }


    public boolean schedule(String  identity,long delayMs,TimerTaskHandler taskHandler){
        boolean succeed =  false;
        try{
            timer.addTask(new TimerTask(identity,delayMs, taskHandler));
            succeed = true;
        }catch (Exception e){
            LOGGER.error("schedule task: {}, delayMs: {} ",taskHandler,delayMs,e);
        }
        return  succeed;
    }



}
