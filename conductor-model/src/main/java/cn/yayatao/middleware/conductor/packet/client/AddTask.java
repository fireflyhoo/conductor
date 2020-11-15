package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 添加延时任务
 */
public class AddTask implements Packet {
    @Override
    public Type getType() {
        return Type.ADD_TASK;
    }
}
