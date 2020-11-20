package cn.yayatao.middleware.conductor.packet.base;

import cn.yayatao.middleware.conductor.packet.Packet;

public class Ping implements Packet {
    private int type = Type.PING.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
