package cn.yayatao.middleware.conductor.client.broker;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.network.netty.NettyClient;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.model.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/****
 * 管理和调度broker的连接
 */
public class BrokerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerManager.class);
    private final ClientConfig config;
    /**
     * 存储当前活着的broker
     */
    private final Set<URL> currentBrokers = Collections.newSetFromMap(new ConcurrentHashMap<>());


    /***
     *
     */
    private final Map<URL, MessageChannel> urlMessageChannelMap = new ConcurrentHashMap<>();


    /***
     *
     */
    private volatile MessageChannel brokerMaster;


    private final MessageChannelHandler channelHandler = new MessageChannelHandler() {
        @Override
        public void connected(MessageChannel channel) throws NetworkException {

        }

        @Override
        public void disconnected(MessageChannel channel) throws NetworkException {

        }

        @Override
        public void received(MessageChannel channel, Object message) throws NetworkException {

        }

        @Override
        public void caught(MessageChannel channel, Throwable throwable) {

        }
    };

    public BrokerManager(ClientConfig config) {
        this.config = config;
    }


    public void addBrokers(List<URL> urls) {
        currentBrokers.addAll(urls);
    }


    public void authBrokers(List<URL> brokers) {
        brokers.stream().forEach(url -> {
            try {
                NettyClient client = new NettyClient(url, channelHandler);
                urlMessageChannelMap.put(url, client);
                client.send(PacketTools.auth(config.getAccessKeyId(),config.getAccessKeySecret()));
            } catch (NetworkException e) {
                LOGGER.error("ask master to broker:{} exception", url, e);
            }
        });
    }


    public MessageChannel getBrokerMaster() {
        return brokerMaster;
    }

    public void setBrokerMaster(MessageChannel brokerMaster) {
        this.brokerMaster = brokerMaster;
    }
}
