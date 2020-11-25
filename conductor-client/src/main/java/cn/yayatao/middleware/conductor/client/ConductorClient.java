package cn.yayatao.middleware.conductor.client;

import cn.yayatao.middleware.conductor.client.broker.BrokerManager;
import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.producer.TaskSender;
import cn.yayatao.middleware.conductor.constant.URLConstant;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.model.URL;
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
 */
public class ConductorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConductorClient.class);
    /***
     * 配置信息
     */
    private final ClientConfig config;
    private final BrokerManager brokerManager;
    private AtomicBoolean running = new AtomicBoolean();
    private CountDownLatch masterBrokerVisibled = new CountDownLatch(1);

    public ConductorClient(ClientConfig config) {
        this.config = config;
        brokerManager = new BrokerManager(config);
    }


    public synchronized void start() {
        if (!running.compareAndSet(false, true)) {
            LOGGER.warn("client has started");
            return;
        }
        final String serverhost = config.getServerHosts();
        if (Strings.isNullOrEmpty(serverhost)) {
            LOGGER.warn("client not assign server host");
            return;
        }
        List<URL> brokers = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(serverhost).stream().map(o -> {
            return URL.valueOf(URLConstant.URL_SERVER_PROTOCOL + "://" + o);
        }).collect(Collectors.toList());

        //认证通过后会返回主节点
        brokerManager.authBrokers(brokers);
        brokerManager.addBrokers(brokers);
        waitMasterBrokerReply();
        subscribeTopicForMaster();

      char[]  arrs = "".toCharArray();

    }


    /****
     * 向主节点master 注册自己订阅
     */
    private void subscribeTopicForMaster() {
    }

    /***
     * 等待主节点回应
     */
    private void waitMasterBrokerReply() {
        try {
           boolean timeOut = !masterBrokerVisibled.await(3, TimeUnit.SECONDS);
           if(timeOut){
               throw new ConductorRuntimeException("can`t connect to  master broker");
           }
        } catch (InterruptedException e) {
            LOGGER.error("线程等待被打断",e);
        }
    }


    /**
     * 获取发送器
     *
     * @return
     */
    public TaskSender getSender() {
        return null;
    }

}
