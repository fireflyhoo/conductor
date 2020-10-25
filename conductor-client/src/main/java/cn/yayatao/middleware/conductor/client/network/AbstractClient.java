package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.model.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractClient implements MessageClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);

    private Lock lock = new ReentrantLock(false);

    /***
     * 是否已经关闭
     */
    protected volatile boolean closed = false;

    /**
     * 是否连成功
     */
    protected volatile boolean connected = false;

    protected final URL url;

    protected  MessageChannelHandler channelHandler;

    private int closeTimeout =  1000;

    private ClientConfig config;


    public AbstractClient(URL url, MessageChannelHandler channelHandler) throws NetworkException{
        this.url = url;
        this.channelHandler = channelHandler;
        try{
            initClient();
        }catch (Throwable e){
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

    protected void connect() throws  NetworkException{
        lock.lock();
        try {
            doConnect();
            connected = true;
        }catch (Throwable e){
            LOGGER.error("连接远程服务出现异常:{} exception",getRemoteAddress(),e);
        }finally {
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


    protected void disconnect(){
        lock.lock();
        try {
            MessageClient channel = getChannel();
            if (channel != null){
                channel.close(closeTimeout);
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
    protected abstract MessageClient getChannel();

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public void send(Object message) throws NetworkException {
        send(message,config.getSendTimeout());
    }

    @Override
    public void send(Object message, long timeout) throws NetworkException {
        getChannel().send(message,timeout);
    }


}
