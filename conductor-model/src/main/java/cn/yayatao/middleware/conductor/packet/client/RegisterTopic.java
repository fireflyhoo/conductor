package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 注册 需要订阅哪些topic
 */
public class RegisterTopic implements Packet {
    @Override
    public Type getType() {
        return Packet.Type.REGISTER_TOPIC;
    }
}
