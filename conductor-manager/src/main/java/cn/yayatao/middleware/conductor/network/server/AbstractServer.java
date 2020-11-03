package cn.yayatao.middleware.conductor.network.server;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class AbstractServer  implements MessageChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServer.class);


    //绑定地址
    private InetSocketAddress bindAddress;
    //服务器最大可接受连接数
    private int accepts;
    //空闲超时时间，单位：毫秒
    private long idleTimeout; //600 seconds

    protected volatile boolean closed = false;
    protected volatile boolean closing = false;


    protected final MessageChannelHandler channelHandler;

    public AbstractServer(InetSocketAddress bindAddress, MessageChannelHandler channelHandler) {
        this.bindAddress = bindAddress;
        this.channelHandler = channelHandler;
        accepts = 65535;
        idleTimeout = 10 * 1000L;
    }


    /**
     * 启动服务
     */
    public void start(){
        try{
            bind();
        }catch (Throwable throwable){
            LOGGER.info("server bind {} excepiton", bindAddress, throwable);
        }
    }



    protected abstract void bind();


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
        this.channelHandler.received(channel,message);
    }

    @Override
    public void caught(MessageChannel channel, Throwable throwable) {
        this.caught(channel,throwable);
    }
}
