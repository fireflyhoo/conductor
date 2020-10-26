package cn.yayatao.middleware.conductor.client.network.netty;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.model.URL;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;


/***
 * 连接管道
 */
public class NettyChannel implements MessageChannel {

    private final Channel channel;

    private final URL url;

    private final MessageChannelHandler channelHandler;

    private long timeout = 2000L;

    private volatile boolean closed = false;

    public NettyChannel(Channel channel, URL url, MessageChannelHandler channelHandler) {
        this.channel = channel;
        this.url = url;
        this.channelHandler = channelHandler;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public void send(Object message) throws NetworkException {
        this.send(message, timeout);
    }

    @Override
    public void send(Object message, long timeout) throws NetworkException {
        if (isClosed()) {
            throw new NetworkException("Failed to send message , this Channel is closed , channel: " + getLocalAddress() + " -> " + getRemoteAddress());
        }
        boolean success = false;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            success = future.await(timeout);
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new NetworkException("Failed to send message " + message + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        if (!success) {
            throw new NetworkException("Failed to send message " + message + " to " + getRemoteAddress() + "in timeout(" + timeout + "ms) limit");
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean isConnected() {
        return !isClosed() && channel.isActive();
    }
}
