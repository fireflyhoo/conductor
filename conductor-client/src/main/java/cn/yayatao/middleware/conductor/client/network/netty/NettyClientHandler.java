package cn.yayatao.middleware.conductor.client.network.netty;

import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.model.URL;
import io.netty.channel.ChannelDuplexHandler;

public class NettyClientHandler extends ChannelDuplexHandler {

    private final URL url ;

    private  final MessageChannelHandler channelHandler;

    public NettyClientHandler(URL url, MessageChannelHandler channelHandler) {
        if (url == null){
            throw new IllegalStateException("url is null");
        }
        if (channelHandler == null){
            throw new IllegalStateException("hanlder is null");
        }
        this.url = url;
        this.channelHandler = channelHandler;
    }
}
