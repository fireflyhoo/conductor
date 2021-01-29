package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/**
 * 取消任务
 * @author fireflyhoo
 */
public class CancelTask extends ClientBase implements Packet {

    private int type = Type.CANCEL_TASK.getValue();

    /**
     * 任务模板ID , 模板定义任务的执行类型,延时,超时,回调方式等
     */
    private String taskTopic;


    /**
     * 任务唯一key
     */
    private String taskKey;


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

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CancelTask{" +
                "type=" + type +
                ", taskTopic='" + taskTopic + '\'' +
                ", taskKey='" + taskKey + '\'' +
                '}';
    }
}
