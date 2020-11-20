package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 添加延时任务
 */
public class AddTask implements Packet {
    private int type = Type.ADD_TASK.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

