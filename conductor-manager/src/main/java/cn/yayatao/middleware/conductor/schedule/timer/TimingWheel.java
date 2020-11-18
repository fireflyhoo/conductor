package cn.yayatao.middleware.conductor.schedule.timer;

import java.util.concurrent.DelayQueue;

/***
 * 时间轮 see kafka
 */
public class TimingWheel {
    /***
     * 每个插槽的时间分片(ms)
     */
    private final long tickMs;

    /****
     * 时间轮大小
     */
    private final int wheelSize;

    /***
     * 时间
     */
    private final long interval;
    /***
     * 延时队列
     */
    private final DelayQueue<TimerTaskList> delayQueue;
    /**
     * 分片
     */
    private final TimerTaskList[] timerTaskLists;
    /***
     * 当前时间
     */
    private long currentTime;
    /***
     *  外层时间轮,即超出时间轮时间范围
     */
    private TimingWheel overflowTimingWheel;


    public TimingWheel(long tickMs, int wheelSizel, long currentTime, DelayQueue<TimerTaskList> delayQueue) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSizel;
        this.interval = tickMs * wheelSizel;
        this.currentTime = currentTime - (currentTime % tickMs);
        this.timerTaskLists = new TimerTaskList[wheelSizel];
        this.delayQueue = delayQueue;
        for (int i = 0; i < wheelSizel; i++) {
            this.timerTaskLists[i] = new TimerTaskList();
        }
    }

    /***
     * 添加延时任务
     * @param timerTask
     * @return
     */
    public boolean addTimerTask(TimerTask timerTask) {
        long expiration = timerTask.getDelayMs();
        //任务过期时间低于第一个时间分片
        if (expiration <= currentTime + tickMs) {
            return false;
        }
        if ((expiration > currentTime + tickMs) && (expiration <= currentTime + interval)) {
            // 添加到当前时间轮的 tick
            long virtualId = expiration / tickMs;
            int index = (int) (virtualId % wheelSize);
            TimerTaskList timerTasks = timerTaskLists[index];
            timerTasks.addTimerTask(timerTask);
            if (timerTasks.setExpiration(virtualId * tickMs)) {
                delayQueue.offer(timerTasks);
            }
        } else {
            getOrCreateOverflowTimingWheel().addTimerTask(timerTask);
        }
        return true;
    }

    private synchronized TimingWheel createOverflowTimingWheel() {
        if (overflowTimingWheel == null) {
            overflowTimingWheel = new TimingWheel(interval, wheelSize, currentTime, delayQueue);
        }
        return overflowTimingWheel;
    }


    private TimingWheel getOrCreateOverflowTimingWheel() {
        if (overflowTimingWheel != null) {
            return overflowTimingWheel;
        }
        return createOverflowTimingWheel();
    }

    public void nextTick(long timestamp) {
        if (timestamp >= currentTime + tickMs) {
            currentTime = timestamp - (timestamp % tickMs);
            if (overflowTimingWheel != null) {
                this.getOverflowTimingWheel().nextTick(timestamp);
            }
        }
    }

    private TimingWheel getOverflowTimingWheel() {
        return overflowTimingWheel;
    }


}
