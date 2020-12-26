package cn.yayatao.middleware.conductor.client.producer;

/**
 * 发送任务回调
 * @author fireflyhoo
 */
public interface SendTaskCallback {

    /**
     * 任务发送成功
     * @param taskTopic
     * @param taskKey
     */
    void succeed(String taskTopic,String taskKey);


    /***
     * 任务发送失败
     * @param taskTopic
     * @param taskKey
     */
    void fail(String taskTopic,String taskKey);
}
