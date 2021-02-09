package cn.yayatao.middleware.conductor.core;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.client.AddTask;
import cn.yayatao.middleware.conductor.packet.client.Authentication;
import cn.yayatao.middleware.conductor.packet.client.CancelTask;
import cn.yayatao.middleware.conductor.packet.client.RegisterTopic;
import cn.yayatao.middleware.conductor.packet.server.AuthenticationResult;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import cn.yayatao.middleware.conductor.utils.ServerPacketTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author fireflyhoo
 */
@Service
public class ScheduleMessageHandler implements MessageChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleMessageHandler.class);

    @Override
    public void connected(MessageChannel channel) throws NetworkException {
        LOGGER.info("网络连接,{}", channel);
    }

    @Override
    public void disconnected(MessageChannel channel) throws NetworkException {
        LOGGER.info("客户端连接断开,{}", channel);
    }

    @Override
    public void received(MessageChannel channel, Object message) throws NetworkException {
        if (message instanceof MessageModel.MessagePacket) {
            Packet packet = ServerPacketTools.analysis((MessageModel.MessagePacket) message);
            doHandleClient(channel, packet);
        } else {
            LOGGER.warn("收到不能理解的客户端数据{},类型 {}", message, message != null ? message.getClass() : "");
        }
    }

    @Override
    public void caught(MessageChannel channel, Throwable throwable) {
        LOGGER.info("消息管道 {}, 出现异常:", channel, throwable);
    }


    /***
     *c 处理客户端请求
     * @param channel
     * @param packet
     */
    private void doHandleClient(MessageChannel channel, Packet packet) throws NetworkException {
        // pong
        if (packet instanceof Pong) {
            // ignore
            return;
        }

        // ping
        if (packet instanceof Ping) {
            channel.send(PacketTools.build("", new Pong()));
            return;
        }

        //认证,返回master
        if (packet instanceof Authentication) {

            AuthenticationResult result = new AuthenticationResult();
            result.setPass(true);
            result.setMasterUrl(new URL("conductor", "127.0.0.1", 8888, new HashMap<>()));
            channel.send(PacketTools.build("", result));
        }


        // 添加定时任务
        if (packet instanceof AddTask) {
            LOGGER.info("收到AddTask数据包:{}", packet);

        }

        // 取消任务
        if (packet instanceof CancelTask) {
            LOGGER.info("收到CancelTask数据包:{}", packet);

        }

        // 注册监听
        if (packet instanceof RegisterTopic) {
            LOGGER.info("注册监听 RegisterTopic: {}", packet);
        }

        //


    }
}
