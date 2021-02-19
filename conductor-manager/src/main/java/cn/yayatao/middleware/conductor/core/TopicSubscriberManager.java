package cn.yayatao.middleware.conductor.core;


import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author fireflyhoo
 */
@Service
public class TopicSubscriberManager {
    /***
     *  (永久, (30s 内有效))
     *  (topic, (group#URL,MessageChannel)
     */
    private final Cache<String, Cache<String, MessageChannel>> cache = Caffeine.newBuilder().build(new CacheLoader<String, Cache<String, MessageChannel>>() {
        @Override
        public @Nullable Cache<String, MessageChannel> load(@NonNull String s) throws Exception {
            // 外部缓存长期有效, 内部缓存 缓存30s
            return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
        }
    });


    /****
     *  注册订阅者 有效期30s
     * @param topic 订阅的主题
     * @param clientGroup 消费群主
     * @param channel 消息通道
     */
    public void registerSubscriber(String topic, String clientGroup, MessageChannel channel) {
        Cache<String, MessageChannel> messageChannel = cache.getIfPresent(topic);
        messageChannel.put(getKey(clientGroup, channel), channel);
    }


    /***
     * 获取唯一key
     * @param clientGroup
     * @param channel
     * @return
     */
    private String getKey(String clientGroup, MessageChannel channel) {
        return String.join("@^@", clientGroup, channel.getURL().toString());
    }

    /***
     *  获取clientGroup@^@url
     * @param key
     * @return
     */
    private String getClientGroup(String key) {
        return key.substring(0, key.indexOf("@^@"));
    }


    /***
     * 取消订阅
     * @param topic 订阅的主题
     * @param clientGroup 消费群主
     * @param channel 消息通道
     */
    public void unRegisterSubscriber(String topic, String clientGroup, MessageChannel channel) {
        Cache<String, MessageChannel> messageChannel = cache.getIfPresent(topic);
        messageChannel.invalidate(getKey(clientGroup, channel));
    }


    /****
     * 获取这个主题的订阅者
     * @param topic 订阅的主题
     * @return
     */
    public List<SubscriberGroup> getSubscriberByGroup(String topic) {
        @Nullable Cache<String, MessageChannel> messageChannel = cache.getIfPresent(topic);
        if (messageChannel == null) {
            return new ArrayList<>();
        }
        // (clientGroup,SubscriberGroup )
        Map<String, SubscriberGroup> subscribers = new HashMap<>();
        for (Map.Entry<String, MessageChannel> channelEntry : messageChannel.asMap().entrySet()) {
            String clientGroup = getClientGroup(channelEntry.getKey());
            SubscriberGroup subscriberGroup = subscribers.get(clientGroup);
            if (subscriberGroup == null) {
                subscriberGroup = new SubscriberGroup();
                subscribers.put(clientGroup, subscriberGroup);
            }
            subscriberGroup.getMessageChannels().add(channelEntry.getValue());
        }
        return new ArrayList<>(subscribers.values());
    }
}
