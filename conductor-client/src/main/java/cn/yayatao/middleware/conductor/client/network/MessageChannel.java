package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.model.URL;

import java.net.InetSocketAddress;

/**
 * 消息通道
 * @author fireflyhoo
 */
public interface MessageChannel {
    /***
     * 获取当前连接的信息
     * @return
     */
    URL getURL();


    /**
     * 获取当前的连接的本地端口
     * @return
     */
    InetSocketAddress getLocalAddress();


    /**
     *  获取远程连接地址信息.
     *
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();


    /***
     * 发送消息
     * @param message
     * @throws NetworkException
     */
    void send(Object message) throws NetworkException;


    /***
     * 发送消息指定发送超时
     * @param message
     * @param timeout
     * @throws NetworkException
     */
    void send(Object message, long timeout) throws  NetworkException;


    /**
     * 是否出错
     *
     * @return closed
     */
    boolean isClosed();

    boolean isConnected();
}
