package cn.yayatao.middleware.conductor.schedule;

public abstract class TimerTaskHandler implements Runnable {

    /***
     * 主键
     */
    private final String identity;

    /**
     * 当前重试次数
     */
    private int retries = 0;


    /***
     * 客户端小组
     */
    private String clientGroup;

    public TimerTaskHandler(String identity, int retries) {
        this.identity = identity;
        this.retries = retries;
    }

    public TimerTaskHandler(String clientGroup, String identity, int retries) {
        this.identity = identity;
        this.retries = retries;
        this.clientGroup = clientGroup;
    }


    public TimerTaskHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public void run() {
        this.execute(identity);
    }

    /**
     * 任务id
     *
     * @return
     */
    public String getIdentity() {
        return identity;
    }


    /****
     * 真正执行任务
     */
    protected abstract void execute(String identity);
}
