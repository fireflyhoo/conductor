package cn.yayatao.middleware.conductor.client;

import cn.yayatao.middleware.conductor.client.broker.BrokerManager;
import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.consumer.TaskExecutorManager;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.producer.TaskSender;
import cn.yayatao.middleware.conductor.client.producer.TaskSenderImpl;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.constant.URLConstant;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.client.RegisterTopic;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 调度客户端
 * @author fireflyhoo
 */
public class ConductorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConductorClient.class);
    /***
     * 配置信息
     */
    private final ClientConfig config;
    private final BrokerManager brokerManager;
    private final TaskExecutorManager taskExecutorManager;
    private AtomicBoolean running = new AtomicBoolean();


    public ConductorClient(ClientConfig config) {
        this.config = config;
        brokerManager = new BrokerManager(config);
        taskExecutorManager = new TaskExecutorManager(config);
    }


    public synchronized void start() {
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("client has started");
            return;
        }
        final String serverHost = config.getServerHosts();
        if (Strings.isNullOrEmpty(serverHost)) {
            LOGGER.warn("client not assign server host");
            return;
        }
        List<URL> brokers = Splitter.on(",").omitEmptyStrings()
                .trimResults().splitToList(serverHost).stream().map(o -> {
            return URL.valueOf(URLConstant.URL_SERVER_PROTOCOL + "://" + o);
        }).collect(Collectors.toList());

        //认证通过后会返回主节点
        brokerManager.authBrokers(brokers);
        brokerManager.addBrokers(brokers);
        brokerManager.waitMasterBrokerReply();
        subscribeTopicForMaster();
    }


    /****
     * 向主节点master 注册自己订阅,准备处理类
     */
    private void subscribeTopicForMaster() {
        RegisterTopic registerTopic = new RegisterTopic();
        registerTopic.setTaskTopic(taskExecutorManager.getAllSubscribeTopics());
        registerTopic.setClientGroup(config.getClientGroup());
        try {
            brokerManager.getBrokerMaster().send(
                    PacketTools.build(config.getAccessKeyId(), registerTopic));
        } catch (NetworkException e) {
            LOGGER.error("订阅topic出现错误");
        }
    }



    /**
     * 获取发送器
     *
     * @return
     */
    public TaskSender getSender() {
        return new TaskSenderImpl(brokerManager,config);
    }

}
