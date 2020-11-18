package cn.yayatao.middleware.conductor.schedule.timer;

/**
 * 定时器任务信息
 */
public class TimerTask implements Comparable<TimerTask> {

    /***
     * 任务唯一标识
     */
    private final String identity;
    /***
     * 延时执行时间点
     */
    private final Long delayMs;
    /***
     *  对应任务
     */
    private final Runnable task;
    /***
     * 任务插槽
     */
    protected TimerTaskList timerTaskList;
    /**
     * 下一个任务节点
     */
    protected TimerTask next;
    /**
     * 上一个任务节点
     */
    protected TimerTask pre;


    public TimerTask(String identity, Long delayMs, Runnable task) {
        this.identity = identity;
        this.delayMs = delayMs;
        this.task = task;
        this.timerTaskList = null;
        this.next = null;
        this.pre = null;
    }

    public String getIdentity() {
        return identity;
    }

    public Long getDelayMs() {
        return delayMs;
    }

    public Runnable getTask() {
        return task;
    }


    @Override
    public int compareTo(TimerTask o) {
        return this.identity.compareTo(o.identity);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimerTask) {
            return this.identity.equals(((TimerTask) obj).identity);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
