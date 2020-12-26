package cn.yayatao.middleware.conductor.client.tools;

import cn.yayatao.middleware.conductor.client.utils.JSONTools;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.client.Authentication;
import cn.yayatao.middleware.conductor.packet.server.AuthenticationResult;
import cn.yayatao.middleware.conductor.packet.server.ErrorResult;
import cn.yayatao.middleware.conductor.packet.server.ExecuteTask;
import cn.yayatao.middleware.conductor.packet.server.MasterChanged;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.UUID;

/***
 * 数据包工具
 */
public class PacketTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketTools.class);


    /**
     * 构造认证数据包
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    public static MessageModel.MessagePacket auth(String accessKeyId, String accessKeySecret) {
        Authentication auth = new Authentication();
        auth.setAccessKeyId(accessKeyId);
        auth.setRandomSalt(UUID.randomUUID().toString());
        auth.setTime(System.currentTimeMillis());
        auth.setSignature(structure(accessKeyId, accessKeySecret, auth.getRandomSalt(), auth.getTime()));
        return MessageModel.MessagePacket.newBuilder().setId(accessKeyId)
                .setType(auth.getType()).setData(JSONTools.toJSON(auth)).build();
    }


    /***
     * 构建参数
     * @param accessKeyId 客户端连接id
     * @param packet 数据包
     * @return
     */
    public static Object build(String accessKeyId, Packet packet) {
        MessageModel.MessagePacket.newBuilder().setId(accessKeyId)
                .setType(packet.getType()).setData(JSONTools.toJSON(packet)).build();
        return null;
    }


    /***
     * 解析数据包
     * @param message
     * @return
     */
    public static Packet analysis(MessageModel.MessagePacket message) {
        Packet.Type type = Packet.Type.valueOf(message.getType());
        switch (type) {
            //认证结果
            case AUTHENTICATION_RESULT:
                return JSONTools.parseObject(message.getData(), AuthenticationResult.class);
            case PONG:
                return JSONTools.parseObject(message.getData(), Pong.class);
            case PING:
                return JSONTools.parseObject(message.getData(), Ping.class);
            case ERR_RESULT:
                return JSONTools.parseObject(message.getData(), ErrorResult.class);
            case EXECUTE_TASK:
                return JSONTools.parseObject(message.getData(), ExecuteTask.class);
            case MASTER_CHANGED:
                return JSONTools.parseObject(message.getData(), MasterChanged.class);
            default:
                return null;
        }
    }


    /**
     * 构建签名
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param randomSalt
     * @param time
     * @return
     */
    private static String structure(String accessKeyId, String accessKeySecret, String randomSalt, long time) {
        StringBuilder sb = new StringBuilder();
        return sha1(sb.append(accessKeyId).append("#").append(accessKeySecret).append("#").append(randomSalt).append("#").append(time).toString());
    }


    public static String sha1(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
            md.update(data.getBytes());
            final StringBuilder buf = new StringBuilder();
            byte[] bits = md.digest();
            for (int bit : bits) {
                int a = bit;
                if (a < 0) {
                    a += 256;
                }
                if (a < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(a));
            }
            return buf.toString();
        } catch (Exception e) {
            LOGGER.error("sha1 异常信息", e);
        }
        return null;
    }


}
