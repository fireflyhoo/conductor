package cn.yayatao.middleware.conductor.packet.base;

import cn.yayatao.middleware.conductor.packet.Packet;

public class Ping implements Packet {
    @Override
    public Type getType() {
        return Type.PING;
    }
}
