package cn.yayatao.middleware.conductor.schedule;

public abstract class TimerTaskHandler implements  Runnable {

    private final String identity;

    public TimerTaskHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public void run() {
        this.execute();
    }

    /**
     * 任务id
     * @return
     */
    public String getIdentity() {
        return identity;
    }


    /****
     * 真正执行任务
     */
    protected abstract void execute();
}
