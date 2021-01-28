package cn.yayatao.middleware.conductor.client.broker;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.consumer.TaskExecutorManager;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.network.netty.NettyClient;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.client.utils.JSONTools;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.server.AuthenticationResult;
import cn.yayatao.middleware.conductor.packet.server.ErrorResult;
import cn.yayatao.middleware.conductor.packet.server.ExecuteTask;
import cn.yayatao.middleware.conductor.packet.server.MasterChanged;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/****
 * 管理和调度broker的连接
 * @author fireflyhoo
 */
public class BrokerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerManager.class);
    private final ClientConfig config;

    private final TaskExecutorManager taskExecutorManager;

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
    private CountDownLatch masterBrokerVisibled = new CountDownLatch(1);
    private final MessageChannelHandler channelHandler = new MessageChannelHandler() {
        @Override
        public void connected(MessageChannel channel) throws NetworkException {
            URL url = channel.getURL();
            MessageChannel _channel = urlMessageChannelMap.get(url);
            if (_channel != channel) {
                urlMessageChannelMap.put(channel.getURL(), _channel);
            }
        }

        @Override
        public void disconnected(MessageChannel channel) throws NetworkException {
            urlMessageChannelMap.remove(channel.getURL());
        }

        @Override
        public void received(MessageChannel channel, Object message) throws NetworkException {
            if (message instanceof MessageModel.MessagePacket) {
                Packet packet = PacketTools.analysis((MessageModel.MessagePacket) message);
                doHandleServerResponse(channel, packet);
            }
        }

        @Override
        public void caught(MessageChannel channel, Throwable throwable) {
            urlMessageChannelMap.remove(channel.getURL());
        }
    };

    public BrokerManager(ClientConfig config,TaskExecutorManager taskExecutorManager) {
        this.config = config;
        this.taskExecutorManager = taskExecutorManager;
    }

    /***
     * 处理服务响应
     * @param channel 消息管道
     * @param packet 数据包
     */
    private void doHandleServerResponse(MessageChannel channel, Packet packet) {
        URL url = channel.getURL();
        MessageChannel _channel = urlMessageChannelMap.get(url);
        if (_channel != channel) {
            urlMessageChannelMap.put(url, channel);
        }
        channel.setLastActivityTime(System.currentTimeMillis());
        if (packet instanceof Pong) {
            // ignore
            return;
        }
        //心跳
        if (packet instanceof Ping) {
            try {
                channel.send(PacketTools.build(config.getAccessKeyId(), new Pong()));
            } catch (NetworkException e) {
                LOGGER.warn("回复心跳出现异常", e);
            }
            return;
        }

        //认证响应
        if (packet instanceof AuthenticationResult) {
            AuthenticationResult authenticationResult = (AuthenticationResult) packet;
            doHandleAuthenticationResult(channel, authenticationResult);
            return;
        }

        //主节点转移
        if (packet instanceof MasterChanged){
            MasterChanged masterChanged = (MasterChanged) packet;
            doHandleMasterChanged(channel,masterChanged);
            return;
        }

        //执行任务
        if (packet instanceof ExecuteTask){
            ExecuteTask executeTask = (ExecuteTask) packet;
            doHandleExecuteTask(channel,executeTask);
            return;
        }

        if (packet instanceof ErrorResult){
            ErrorResult errorResult = (ErrorResult) packet;
            doHandleErrorResult(channel,errorResult);
        }



    }


    /****
     * 错误信息
     * @param channel
     * @param errorResult
     */
    private void doHandleErrorResult(MessageChannel channel, ErrorResult errorResult) {
        LOGGER.error("服务出错信息:{}" , errorResult.getMsg());
    }

    /***
     * 执行错误信息
     * @param channel
     * @param executeTask
     */
    private void doHandleExecuteTask(MessageChannel channel, ExecuteTask executeTask) {
        taskExecutorManager.executeTask(channel,executeTask);
    }


    /***
     * 处理主节点改变的事件
     * @param channel
     * @param masterChanged
     */
    private void doHandleMasterChanged(MessageChannel channel, MasterChanged masterChanged) {
        URL masterURL = masterChanged.getMasterUrl();
        if(masterURL != null && masterURL.equals(channel.getURL())){
            LOGGER.info("now crurrent broker[{}] think change current master to : {} ", channel.getURL(), masterURL);
            setBrokerMaster(channel);
        }
    }


    /***
     * 处理认证响应成功的代码
     * @param channel
     * @param authenticationResult
     */
    private void doHandleAuthenticationResult(MessageChannel channel, AuthenticationResult authenticationResult) {
        if (authenticationResult.isPass()) {
            URL masterURL = authenticationResult.getMasterUrl();
            if (masterURL != null && masterURL.equals(channel.getURL())) {
                LOGGER.info("broker[{}] think current master is: {} ", channel.getURL(), masterURL);
                //当前链接为主节点
                setBrokerMaster(channel);
                masterBrokerVisibled.countDown();
            } else {
                LOGGER.info("broker[{}] can't think current master broker  survival", channel.getURL());
            }
        } else {
            LOGGER.error("auth broker error : {}", JSONTools.toJSON(authenticationResult));
            throw new ConductorRuntimeException("the broker cannot auth");
        }
    }

    public void addBrokers(List<URL> urls) {
        currentBrokers.addAll(urls);
    }


    public void authBrokers(List<URL> brokers) {
        brokers.stream().forEach(url -> {
            try {
                NettyClient client = new NettyClient(url, channelHandler);
                urlMessageChannelMap.put(url, client);
                client.send(PacketTools.auth(config.getAccessKeyId(), config.getAccessKeySecret()));
            } catch (NetworkException e) {
                LOGGER.error("ask master to broker:{} exception", url, e);
            }
        });
    }

    /***
     * 等待主节点回应
     */
    public void waitMasterBrokerReply() {
        try {
            boolean timeOut = !masterBrokerVisibled.await(3, TimeUnit.SECONDS);
            if (timeOut) {
              //  throw new ConductorRuntimeException("can`t connect to  master broker");
            }
        } catch (InterruptedException e) {
            LOGGER.error("线程等待被打断", e);
        }
    }


    public MessageChannel getBrokerMaster() {
        return brokerMaster;
    }

    public void setBrokerMaster(MessageChannel brokerMaster) {
        this.brokerMaster = brokerMaster;
    }
}
