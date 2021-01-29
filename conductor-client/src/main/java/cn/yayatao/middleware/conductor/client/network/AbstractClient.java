package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.model.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractClient implements MessageClient, MessageChannelHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);
    protected final URL url;
    /***
     * 是否已经关闭
     */
    protected volatile boolean closed = false;

    /**
     * 是否连成功
     */
    protected volatile boolean connected = false;
    protected MessageChannelHandler channelHandler;
    private final Lock lock = new ReentrantLock(false);
    private final int closeTimeout = 1000;

    private final ClientConfig config;


    public AbstractClient(URL url, MessageChannelHandler channelHandler, ClientConfig config) throws NetworkException {
        this.url = url;
        this.channelHandler = channelHandler;
        this.config = config;
        try {
            initClient();
        } catch (Throwable e) {
            e.printStackTrace();
            close(closeTimeout);
        }
    }

    /**
     * 初始化客户端
     */
    protected abstract void initClient();


    /***
     * 进行连接
     */
    protected abstract void doConnect() throws NetworkException;

    protected void connect() throws NetworkException {
        lock.lock();
        try {
            doConnect();
            connected = true;
        } catch (Throwable e) {
            LOGGER.error("连接远程服务出现异常:{} exception", getRemoteAddress(), e);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void reconnect() throws NetworkException {
        // 断开连接
        disconnect();
        // 发起连接
        connect();
    }


    protected void disconnect() {
        lock.lock();
        try {
            MessageChannel channel = getChannel();
            if (channel != null) {
                channel.close();
            }
            try {
                doDisconnect();
            } catch (Throwable ex) {
                LOGGER.warn("close ");
            }
        } finally {
            lock.unlock();
        }
    }


    /***
     * 进行连接
     */
    protected abstract void doDisconnect();


    /***
     * 获取消息连接
     * @return
     */
    protected abstract MessageChannel getChannel();

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public void send(Object message) throws NetworkException {
        send(message, config.getSendTimeout());
    }


    @Override
    public void send(Object message, long timeout) throws NetworkException {
        //未连接时, 发起连接
        if (!isConnected()) {
            connect();
        }
        MessageChannel messageChannel = getChannel();
        if (messageChannel == null) {
            throw new NetworkException("message can not send , because channel is closed , url :" + getURL());
        }
        //通过通道发送消息
        messageChannel.send(message, timeout);
    }

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
    public boolean isConnected() {
        MessageChannel channel = getChannel();
        return this.connected && channel != null && channel.isConnected();
    }
}
