package cn.yayatao.middleware.conductor.client.config;

/****
 * 调度服务客户端配置
 */
public class ClientConfig {
    /**
     * 调度服务地址信息 localhost:1111,localhost:2222,localhost:3333
     */
    private String serverHosts;

    /***
     * 任务执行是主动ack
     */
    private Boolean executedAutoCommit = true;


    /***
     * 访问授权码id
     */
    private String accessKeyId;


    /***
     * 访问授权码秘钥
     */
    private String accessKeySecret;
    /***
     * 客户端组
     */
    private String clientGroup;
    /***
     * 启动消费
     */
    private boolean consumeEnable;
    /**
     * 发送超时
     */
    private long sendTimeout = 2000L;
    /**
     * 连接重试次数
     */
    private int retryTimes = 3;
    /***
     * 连接超时时间
     */
    private long connectTimeout;
    /***
     * 心跳间隔时间
     */
    private long heartbeatInterval = 100000;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

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

    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}
