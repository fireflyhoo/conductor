package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

import java.util.List;

/***
 * 注册 需要订阅哪些topic
 * @author fireflyhoo
 */
public class RegisterTopic implements Packet {

    private int type = Packet.Type.REGISTER_TOPIC.getValue();

    /**
     * 任务模板ID , 模板定义任务的执行类型,延时,超时,回调方式等
     */
    private List<String>taskTopic;


    /***
     * 订阅组 (为兼容一个topic多个服务监听的情况)
     */
    private String clientGroup;



    public List<String> getTaskTopic() {
        return taskTopic;
    }

    public void setTaskTopic(List<String> taskTopic) {
        this.taskTopic = taskTopic;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }
}
