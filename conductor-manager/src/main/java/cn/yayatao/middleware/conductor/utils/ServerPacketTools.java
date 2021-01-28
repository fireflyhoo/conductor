package cn.yayatao.middleware.conductor.utils;

import cn.yayatao.middleware.conductor.client.utils.JSONTools;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.client.*;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;

/**
 * 服务端数据工具类
 */
public class ServerPacketTools {


    /***
     * 解析数据包
     * @param message
     * @return
     */
    public static Packet analysis(MessageModel.MessagePacket message) {
        Packet.Type type = Packet.Type.valueOf(message.getType());
        switch (type) {
            case PING:
                return JSONTools.parseObject(message.getData(), Ping.class);
            case PONG:
                return JSONTools.parseObject(message.getData(), Pong.class);
            case AUTHENTICATION:
                return JSONTools.parseObject(message.getData(), Authentication.class);
            case ADD_TASK:
                return JSONTools.parseObject(message.getData(), AddTask.class);
            case ACK_EXECUTE_TASK:
                return JSONTools.parseObject(message.getData(), AckExecuteTask.class);
            case CANCEL_TASK:
                return JSONTools.parseObject(message.getData(), CancelTask.class);
            case REGISTER_TOPIC:
                return JSONTools.parseObject(message.getData(), RegisterTopic.class);
            default:
                throw new IllegalArgumentException("MessagePacket type unknown");
        }
    }
}
