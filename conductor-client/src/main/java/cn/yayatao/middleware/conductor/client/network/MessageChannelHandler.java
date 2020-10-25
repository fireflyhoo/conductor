package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;

/**
 * 消息管道事件处理
 * @author fireflyhoo
 */
public interface MessageChannelHandler {

    /****
     * 消息管道连接成功
     * @param channel
     * @throws NetworkException
     */
    void connected(MessageChannel channel) throws NetworkException;

    /****
     * 断开连接
     * @param channel
     * @throws NetworkException
     */
    void disconnected(MessageChannel channel) throws NetworkException;


    /***
     * 消息接收到
     * @param channel
     * @param message
     * @throws NetworkException
     */
    void received(MessageChannel channel, Object message) throws  NetworkException;


    /***
     * 出现异常
     * @param channel
     * @param throwable
     */
    void caught(MessageChannel channel, Throwable throwable);
}
