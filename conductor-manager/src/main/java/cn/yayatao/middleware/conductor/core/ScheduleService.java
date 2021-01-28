package cn.yayatao.middleware.conductor.core;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.network.server.NettyServer;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.client.AddTask;
import cn.yayatao.middleware.conductor.packet.client.Authentication;
import cn.yayatao.middleware.conductor.packet.server.AuthenticationResult;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import cn.yayatao.middleware.conductor.schedule.TaskScheduler;
import cn.yayatao.middleware.conductor.utils.ServerPacketTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.HashMap;


/**
 * 调度服务
 *
 * @author fireflyhoo
 */
@Service
public class ScheduleService implements InitializingBean, DisposableBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    /**
     * 调度核心
     */
    private final TaskScheduler taskScheduler = new TaskScheduler();

    private final NettyServer nettyServer = new NettyServer(new InetSocketAddress(8888), new ScheduleMessageHandler());

    @Override
    public void destroy() throws Exception {
        //销毁
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化
        start();
    }

    private void stop() {
        this.nettyServer.stop();
        this.taskScheduler.stop();
    }

    private void start() {
        this.nettyServer.start();
        this.taskScheduler.start();
        LOGGER.info("调度启动信息!");
    }


    /***
     *c 处理客户端请求
     * @param channel
     * @param packet
     */
    private void doHandleClient(MessageChannel channel, Packet packet) throws NetworkException {
        if (packet instanceof Pong) {
            // ignore
            return;
        }
        if (packet instanceof Ping) {
            channel.send(PacketTools.build("", new Pong()));
            return;
        }

        if (packet instanceof Authentication) {
            AuthenticationResult result = new AuthenticationResult();
            result.setPass(true);
            result.setMasterUrl(new URL("conductor", "127.0.0.1", 8888, new HashMap<>()));
            channel.send(PacketTools.build("", new AuthenticationResult()));
        }

        if (packet instanceof AddTask){
            LOGGER.info("收到AddTask数据包:",packet);
        }
    }


    class ScheduleMessageHandler implements MessageChannelHandler {

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
    }
}
