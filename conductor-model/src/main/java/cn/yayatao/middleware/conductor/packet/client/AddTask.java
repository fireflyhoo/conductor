package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 添加延时任务
 * @author fireflyhoo
 */
public class AddTask extends ClientBase implements Packet {
    private int type = Type.ADD_TASK.getValue();

    //任务信息
    private Task task;

    @Override
    public int getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AddTask{" +
                "type=" + type +
                ", task=" + task +
                '}';
    }
}

