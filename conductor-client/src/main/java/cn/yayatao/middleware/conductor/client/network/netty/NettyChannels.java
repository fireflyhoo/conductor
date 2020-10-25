package cn.yayatao.middleware.conductor.client.network.netty;

import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.model.URL;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储管道信息
 */
public class NettyChannels {
    private final static Map<Channel, MessageChannel> channelMap = new ConcurrentHashMap<>();

    public static MessageChannel getOrAddChannel(Channel channel, URL url, MessageChannelHandler channelHandler) {
        if (channel == null) {
            return null;
        }
        MessageChannel ret = channelMap.get(channel);
        if (ret != null) {
            return ret;
        }
        MessageChannel messageChannel = new NettyChannel(channel, url, channelHandler);
        if (channel.isActive()) {
            ret = channelMap.put(channel, messageChannel);
        }
        if (ret == null) {
            ret = messageChannel;
        }
        return ret;
    }


    public static void removeChannelIfDisconnected(Channel channel) {
        if (channel != null && !channel.isActive()) {
            channelMap.remove(channel);
        }
    }


}
