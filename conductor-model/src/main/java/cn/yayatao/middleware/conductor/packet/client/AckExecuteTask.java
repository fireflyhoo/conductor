package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/****
 * 任务执行结果上报
 */
public class AckExecuteTask implements Packet {

    private int type = Packet.Type.ACK_EXECUTE_TASK.getValue();

    /**
     * 任务模板ID , 模板定义任务的执行类型,延时,超时,回调方式等
     */
    private String taskTopic;


    /**
     * 任务唯一key
     */
    private String taskKey;

    /***
     * 执行结果
     */
    private boolean succeed;

    public String getTaskTopic() {
        return taskTopic;
    }

    public void setTaskTopic(String taskTopic) {
        this.taskTopic = taskTopic;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
