package cn.yayatao.middleware.conductor.client.config;

/****
 * 调度服务客户端配置
 */
public class ClientConfig {
    /**
     * 调度服务地址信息 localhost:1111,localhost:2222,localhost:3333
     */
    private  String serverHosts;

    /***
     * 任务执行是主动ack
     */
    private  Boolean executedAutoCommit = true;


    /***
     * 客户端组
     */
    private  String clientGroup;


    /***
     * 启动消费
     */
    private  boolean consumeEnable;


    /**
     * 发送超时
     */
    private long sendTimeout = 2000L;

    /**
     * 连接重试次数
     */
    private int retryTimes;



    /***
     * 连接超时时间
     */
    private long connectTimeout;

    /***
     * 心跳间隔时间
     */
    private long heatbeatInterval;



    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getServerHosts() {
        return serverHosts;
    }

    public void setServerHosts(String serverHosts) {
        this.serverHosts = serverHosts;
    }

    public Boolean getExecutedAutoCommit() {
        return executedAutoCommit;
    }

    public void setExecutedAutoCommit(Boolean executedAutoCommit) {
        this.executedAutoCommit = executedAutoCommit;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public boolean isConsumeEnable() {
        return consumeEnable;
    }

    public void setConsumeEnable(boolean consumeEnable) {
        this.consumeEnable = consumeEnable;
    }

    public long getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(long sendTimeout) {
        this.sendTimeout = sendTimeout;
    }


    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getHeatbeatInterval() {
        return heatbeatInterval;
    }

    public void setHeatbeatInterval(long heatbeatInterval) {
        this.heatbeatInterval = heatbeatInterval;
    }
}
