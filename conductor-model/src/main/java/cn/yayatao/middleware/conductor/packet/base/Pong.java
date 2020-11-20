package cn.yayatao.middleware.conductor.packet.base;

import cn.yayatao.middleware.conductor.packet.Packet;

public class Pong implements Packet {

    private int type = Type.PONG.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
