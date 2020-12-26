package cn.yayatao.middleware.conductor.model;

/**
 * 任务主题
 * @author fireflyhoo
 */
public class TaskTopic {

    /**
     * 主题名称
     */
    private String topicName;


    /***
     * 主题说明
     */
    private String  topicDesc;

    /***
     * 延时时间(ms)
     */
    private Long  delay;


    /**
     * 执行超时时间
     */
    private long timeout;


    /**
     * 重试次数
     */
    private  int retry;


    /**
     * 回调类型, rpc/http
     */
    private  String callType;


    /**
     * 回到地址, rpc 模式为空
     */
    private  String callUrl;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDesc() {
        return topicDesc;
    }

    public void setTopicDesc(String topicDesc) {
        this.topicDesc = topicDesc;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallUrl() {
        return callUrl;
    }

    public void setCallUrl(String callUrl) {
        this.callUrl = callUrl;
    }
}
