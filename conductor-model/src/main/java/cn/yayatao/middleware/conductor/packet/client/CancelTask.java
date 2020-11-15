package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

public class CancelTask implements Packet {
    @Override
    public Type getType() {
        return Type.CANCEL_TASK;
    }
}
