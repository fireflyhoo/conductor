package cn.yayatao.middleware.conductor.schedule.timer;

import cn.yayatao.middleware.conductor.common.LifeCycle;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.thread.NamedThreadFactory;
import com.google.common.base.Preconditions;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/***
 * 定时器核心,用于任务调度
 */
public class Timer implements LifeCycle {

    public static final int WORKER_STATE_INIT = 0;
    public static final int WORKER_STATE_STARTED = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;

    /***
     * 核心线程
     */
    private final Thread bossThread;
    /***
     * 任务执行线程
     */
    private final ExecutorService workerThreadPool;
    /***
     * 是否完成初始化
     */
    private final CountDownLatch startTimeInitialized = new CountDownLatch(1);
    /***
     * 启动状态
     */
    private final AtomicBoolean started = new AtomicBoolean(false);
    /**
     * 最小时间刻度的时间轮
     */
    private final TimingWheel lowestWheel;
    /***
     * 最小时间轮的刻度时间
     */
    private final long lowestTickMs;
    /***
     * 最小时间轮大小
     */
    private final int lowestWheelSize;
    private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();
    /**
     * 定时器状态
     */
    private final AtomicInteger workerState = new AtomicInteger(WORKER_STATE_INIT);

    public Timer(long lowestTickMs, int lowestWheelSize) {
        this(lowestTickMs, lowestWheelSize, new NamedThreadFactory("multi-timer-boss"));
    }

    public Timer(long lowestTickMs, int lowestWheelSize, NamedThreadFactory namedThreadFactory) {
        Preconditions.checkArgument(lowestTickMs > 0, "lowestTickMs must  greater than zero");
        Preconditions.checkArgument(lowestWheelSize > 0, "lowestWheelSize must greater than zero");
        Preconditions.checkArgument(lowestWheelSize < 1L << 30, "lowestWheelSize must little then 2^30");
        this.lowestTickMs = lowestTickMs;
        this.lowestWheelSize = lowestWheelSize;
        this.lowestWheel = new TimingWheel(lowestTickMs, lowestWheelSize, System.currentTimeMillis(), delayQueue);
        this.bossThread = namedThreadFactory.newThread(new BossWorker());
        int workerSize = Math.max(2, Runtime.getRuntime().availableProcessors() * 4);
        workerThreadPool = new ThreadPoolExecutor(workerSize, workerSize, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(), new NamedThreadFactory("multi-timer-worker"));
    }

    @Override
    public void start() {
        if (workerState.get() == WORKER_STATE_INIT) {
            workerState.compareAndSet(WORKER_STATE_INIT, WORKER_STATE_STARTED);
            bossThread.start();
            while (!started.get()){
                try {
                    this.startTimeInitialized.await();
                } catch (InterruptedException e) {
                    // Ignore - it will be ready very soon.
                }
            }
            return ;
        }
        if (workerState.get() == WORKER_STATE_STARTED) {
            return;
        }
        if (workerState.get() == WORKER_STATE_SHUTDOWN) {
            throw new IllegalStateException("can't be started once stopped");
        }
        throw new IllegalStateException("Invalid  WorkerState");


    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    class BossWorker implements Runnable {
        @Override
        public void run() {
            started.compareAndSet(false,true);
            startTimeInitialized.countDown();
            do {
                Timer.this.execute();
                System.out.println("execute");
            }while (workerState.get() == WORKER_STATE_STARTED);
        }
    }

    /****
     * 循环执行任务
     */
    private void execute() {
        try{
            TimerTaskList timerTaskList = delayQueue.poll(this.lowestTickMs, TimeUnit.MILLISECONDS);
            if(timerTaskList != null){
                lowestWheel.nextTick(timerTaskList.getExpiration());
                //执行过期任务
                timerTaskList.pop(this::addTask);
            }
        }catch (Exception e){
            //NOOP
        }

    }

    /***
     * 添加定时任务
     * @param timerTask
     */
    public void addTask(TimerTask timerTask) {
        if(workerState.get() != WORKER_STATE_STARTED){
            throw  new ConductorRuntimeException("timer not runing");
        }
        if(!lowestWheel.addTimerTask(timerTask)){
            workerThreadPool.submit(timerTask.getTask());
        }
    }
}
