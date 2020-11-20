package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 注册 需要订阅哪些topic
 */
public class RegisterTopic implements Packet {
    private int type = Packet.Type.REGISTER_TOPIC.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
