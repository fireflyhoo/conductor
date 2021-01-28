package cn.yayatao.middleware.conductor.network.server;

import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.network.netty.NettyChannel;
import cn.yayatao.middleware.conductor.client.network.netty.NettyChannels;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.network.NetUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  服务处理类
 * @author fireflyhoo
 */
@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private final Map<String, MessageChannel> channels = new ConcurrentHashMap<>();

    private final MessageChannelHandler channelHandler;
    private final URL url;


    public NettyServerHandler(URL url, MessageChannelHandler channelHandler) {
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        NettyChannel channel = (NettyChannel) NettyChannels.getOrAddChannel(ctx.channel(), url, channelHandler);
        try {
            if (channel != null) {
                channels.put(NetUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()), channel);
            }
            channelHandler.connected(channel);
        } finally {
            NettyChannels.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = (NettyChannel) NettyChannels.getOrAddChannel(ctx.channel(), url, channelHandler);
        try {
            channels.remove(NetUtil.toAddressString((InetSocketAddress) ctx.channel().remoteAddress()));
            channelHandler.disconnected(channel);
        } finally {
            NettyChannels.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyChannel channel = (NettyChannel) NettyChannels.getOrAddChannel(ctx.channel(), url, channelHandler);
        try {
            channelHandler.caught(channel, cause);
        } finally {
            NettyChannels.removeChannelIfDisconnected(ctx.channel());
        }
    }

    public Map<String, MessageChannel> getChannels() {
        return this.channels;
    }
}
