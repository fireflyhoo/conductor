package cn.yayatao.middleware.conductor.client.network;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;

public interface MessageClient extends  MessageChannel{

    /**
     * 重新连接
     * @throws NetworkException
     */
    void reconnect() throws NetworkException;

    /***
     * 关闭通道
     * @param closeTimeout
     */
    void close(int closeTimeout);

}
