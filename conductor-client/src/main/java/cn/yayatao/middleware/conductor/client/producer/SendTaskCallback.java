package cn.yayatao.middleware.conductor.client.producer;

public interface SendTaskCallback {

    /**
     * 任务发送成功
     * @param taskTemplate
     * @param taskKey
     */
    void succeed(String taskTemplate,String taskKey);


    /***
     * 任务发送失败
     * @param taskTemplate
     * @param taskKey
     */
    void fail(String taskTemplate,String taskKey);
}
