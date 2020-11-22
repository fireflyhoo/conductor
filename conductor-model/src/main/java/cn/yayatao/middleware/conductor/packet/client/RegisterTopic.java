package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 注册 需要订阅哪些topic
 */
public class RegisterTopic implements Packet {
    private int type = Packet.Type.REGISTER_TOPIC.getValue();

    /**
     * 任务模板ID , 模板定义任务的执行类型,延时,超时,回调方式等
     */
    private String taskTopic;

    public String getTaskTopic() {
        return taskTopic;
    }

    public void setTaskTopic(String taskTopic) {
        this.taskTopic = taskTopic;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
