package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/****
 * 任务执行结果上报
 */
public class AckExecuteTask  implements Packet {

    @Override
    public Type getType() {
        return Packet.Type.ACK_EXECUTE_TASK;
    }
}
