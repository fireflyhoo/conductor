package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.model.URL;

import java.net.InetSocketAddress;

/**
 * 消息通道
 *
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
     *
     * @return
     */
    InetSocketAddress getLocalAddress();


    /**
     * 获取远程连接地址信息.
     *
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /***
     *  获取最后活动时间
     * @return
     */
    long getLastActivityTime();

    /***
     * 设置最后活动时间
     * @param lastActivityTime 最后活动时间戳
     */
    void setLastActivityTime(long lastActivityTime);

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
    void send(Object message, long timeout) throws NetworkException;


    /**
     * 是否出错
     *
     * @return closed
     */
    boolean isClosed();


    /**
     * 是否连接成功
     *
     * @return
     */
    boolean isConnected();


    /**
     * 关闭连接
     *
     * @return
     */
    void close();
}
