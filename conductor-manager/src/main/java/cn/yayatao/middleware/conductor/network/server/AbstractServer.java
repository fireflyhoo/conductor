package cn.yayatao.middleware.conductor.network.server;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class AbstractServer implements MessageChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServer.class);
    protected final MessageChannelHandler channelHandler;
    protected volatile boolean closed = false;
    protected volatile boolean closing = false;
    /***
     *     绑定地址
     */
    private final InetSocketAddress bindAddress;
    /**
     * 服务器最大可接受连接数
     */
    private final int accepts;
    /***
     * 空闲超时时间，单位：毫秒  600 seconds
     */
    private final long idleTimeout;

    public AbstractServer(InetSocketAddress bindAddress, MessageChannelHandler channelHandler) {
        this.bindAddress = bindAddress;
        this.channelHandler = channelHandler;
        accepts = 65535;
        idleTimeout = 10 * 1000L;
    }


    /**
     * 启动服务
     */
    public void start() {
        try {
            bind(bindAddress);
        } catch (Throwable throwable) {
            LOGGER.info("server bind {} excepiton", bindAddress, throwable);
        }
    }


    /***
     * 真实绑定网络
     * @param bindAddress
     */
    protected abstract void bind(InetSocketAddress bindAddress);


    @Override
    public void connected(MessageChannel channel) throws NetworkException {
        this.channelHandler.connected(channel);
    }

    @Override
    public void disconnected(MessageChannel channel) throws NetworkException {
        this.channelHandler.disconnected(channel);
    }

    @Override
    public void received(MessageChannel channel, Object message) throws NetworkException {
        this.channelHandler.received(channel, message);
    }

    @Override
    public void caught(MessageChannel channel, Throwable throwable) {
        LOGGER.warn("连接出现异常 {},", channel, throwable);
    }

    public abstract void stop();
}
