package cn.yayatao.middleware.conductor;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.network.server.NettyServer;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;

import java.net.InetSocketAddress;

public class NettyServerTest {
    public static void main(String[] args) {

        NettyServer nettyServer = new NettyServer(new InetSocketAddress(6666), new MessageChannelHandler() {
            @Override
            public void connected(MessageChannel channel) throws NetworkException {
                System.out.println("连接成功 :" + channel);
            }

            @Override
            public void disconnected(MessageChannel channel) throws NetworkException {
                System.out.println("连接断开 : " +channel);
            }

            @Override
            public void received(MessageChannel channel, Object message) throws NetworkException {
                System.out.println("收到消息 : " +channel +  message );
                channel.send(MessageModel.MessagePacket.newBuilder().setType(1).setData("你妹吖"));
            }

            @Override
            public void caught(MessageChannel channel, Throwable throwable) {
                System.out.println("出现异常 : " +channel +  throwable );
            }
        });
        nettyServer.start();
    }
}
