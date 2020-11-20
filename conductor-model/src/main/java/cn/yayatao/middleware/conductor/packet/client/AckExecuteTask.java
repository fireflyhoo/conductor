package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/****
 * 任务执行结果上报
 */
public class AckExecuteTask  implements Packet {

    private int type =  Packet.Type.ACK_EXECUTE_TASK.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
