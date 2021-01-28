package cn.yayatao.middleware.conductor.client.network.netty;

import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.model.URL;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

@ChannelHandler.Sharable
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


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        NettyChannel channel = (NettyChannel) NettyChannels.getOrAddChannel(ctx.channel(), url, channelHandler);
        NettyChannels.removeChannelIfDisconnected(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel channel = (NettyChannel) NettyChannels.getOrAddChannel(ctx.channel(), url, channelHandler);
        try {
            channelHandler.received(channel, msg);
        } finally {
            NettyChannels.removeChannelIfDisconnected(ctx.channel());
        }

    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }


}
