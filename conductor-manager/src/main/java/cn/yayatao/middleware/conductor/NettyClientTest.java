package cn.yayatao.middleware.conductor;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.network.netty.NettyClient;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.protobuf.MessagePacketModel;

import java.io.IOException;
import java.util.HashMap;

public class NettyClientTest {
    public static void main(String[] args) throws NetworkException, IOException {
        NettyClient nettyClient = new NettyClient(new URL("bbq", "127.0.0.1", 6666, new HashMap<>()), new MessageChannelHandler() {
            @Override
            public void connected(MessageChannel channel) throws NetworkException {

            }

            @Override
            public void disconnected(MessageChannel channel) throws NetworkException {

            }

            @Override
            public void received(MessageChannel channel, Object message) throws NetworkException {
                System.out.println("文件处理我就是试试:" + message);
            }

            @Override
            public void caught(MessageChannel channel, Throwable throwable) {

            }
        });

        nettyClient.send(MessagePacketModel.MessagePacket.newBuilder().setId("66666").setData("heheheh").setType(1));
        System.in.read();
        System.in.read();

    }
}
