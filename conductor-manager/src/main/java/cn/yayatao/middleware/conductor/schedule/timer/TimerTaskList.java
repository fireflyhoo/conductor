package cn.yayatao.middleware.conductor.schedule.timer;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 每一个时间轮分片的任务列表
 */
public class TimerTaskList implements Iterable<TimerTask>, Delayed {

    /***
     * 链表头节点
     */
    private final TimerTask root;
    /***
     * 任务分片锁(非公平)
     */
    private final ReentrantLock unfairLock = new ReentrantLock(false);
    /***
     * 过期时间
     */
    private AtomicLong expiration = new AtomicLong(-1L);


    public TimerTaskList() {
        root = new TimerTask("ROOT", -1L, null);
        root.next = root;
        root.pre = root;
    }

    public boolean setExpiration(long expireMs) {
        return expiration.getAndSet(expireMs) != expireMs;
    }

    public long getExpiration() {
        return expiration.get();
    }

    public void setExpiration(AtomicLong expiration) {
        this.expiration = expiration;
    }


    /***
     * 添加任务
     * @param timerTask
     */
    public void addTimerTask(TimerTask timerTask) {
        unfairLock.lock();
        try {
            if (timerTask.timerTaskList == null) {
                timerTask.timerTaskList = this;
                final TimerTask tail = root.pre;
                //插入队尾
                timerTask.pre = tail;
                timerTask.next = root;
                tail.next = timerTask;
                root.pre = timerTask;
            }
        } finally {
            unfairLock.unlock();
        }
    }

    /***
     * 删除任务
     * @param timerTask
     */
    private void removeTimerTask(TimerTask timerTask) {
        unfairLock.lock();
        try {
            if (timerTask.timerTaskList.equals(this)) {
                TimerTask pre = timerTask.pre;
                TimerTask next = timerTask.next;
                pre.next = next;
                next.pre = pre;
                timerTask.timerTaskList = null;
                timerTask.next = null;
                timerTask.pre = null;
            }
        } finally {
            unfairLock.unlock();
        }
    }


    /**
     * 弹出任务
     *
     * @param consumer
     */
    public void pop(Consumer<TimerTask> consumer) {
        TimerTask timerTask = root.next;
        while (!timerTask.equals(root)) {
            this.removeTimerTask(timerTask);
            consumer.accept(timerTask);
            timerTask = root.next;
        }
        expiration.set(-1L);
    }


    @Override
    public int compareTo(Delayed o) {
        if (o instanceof TimerTaskList) {
            long expire = this.expiration.get();
            return Long.compare(expire, ((TimerTaskList) o).getExpiration());
        }
        return 0;
    }

    @Override
    public Iterator<TimerTask> iterator() {
        List<TimerTask> collection = Lists.newLinkedList();
        TimerTask first = root;
        while (!first.next.equals(root)) {
            TimerTask node = first.next;
            if (node.getDelayMs() >= 0 && node.getTask() != null) {
                collection.add(node);
            }
            first = node;
        }
        return collection.iterator();
    }


    @Override
    public long getDelay(TimeUnit unit) {
        long expire = this.expiration.get();
        return Math.max(0, unit.convert(expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }
}
