package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 执行任务命令
 */
public class ExecuteTask  implements Packet{
    private int type = Packet.Type.EXECUTE_TASK.getValue();

    private Task task;

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
