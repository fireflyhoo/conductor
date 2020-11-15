package cn.yayatao.middleware.conductor.packet.base;

import cn.yayatao.middleware.conductor.packet.Packet;

public class Pong implements Packet {
    @Override
    public Type getType() {
        return Type.PONG;
    }
}
