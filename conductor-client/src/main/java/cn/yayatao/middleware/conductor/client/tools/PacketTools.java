package cn.yayatao.middleware.conductor.client.tools;

import cn.yayatao.middleware.conductor.client.utils.JSONTools;
import cn.yayatao.middleware.conductor.packet.client.Authentication;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;

import java.util.UUID;

/***
 * 数据包工具
 */
public class PacketTools {

    public static MessageModel.MessagePacket auth(String accessKeyId) {
        Authentication auth = new Authentication();
        auth.setAccessKeyId(accessKeyId);
        auth.setRandomSalt(UUID.randomUUID().toString());
        auth.setSignature("");
        return MessageModel.MessagePacket.newBuilder().setType(auth.getType()).setData(JSONTools.toJSON(auth)).build();
    }
}
