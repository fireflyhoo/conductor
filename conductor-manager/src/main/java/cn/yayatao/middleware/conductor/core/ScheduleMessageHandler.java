package cn.yayatao.middleware.conductor.core;

import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.packet.base.Pong;
import cn.yayatao.middleware.conductor.packet.client.AddTask;
import cn.yayatao.middleware.conductor.packet.client.Authentication;
import cn.yayatao.middleware.conductor.packet.client.CancelTask;
import cn.yayatao.middleware.conductor.packet.client.RegisterTopic;
import cn.yayatao.middleware.conductor.packet.server.AuthenticationResult;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import cn.yayatao.middleware.conductor.schedule.TaskScheduler;
import cn.yayatao.middleware.conductor.schedule.TimerTaskHandler;
import cn.yayatao.middleware.conductor.storage.TaskMessageStoreManager;
import cn.yayatao.middleware.conductor.utils.ServerPacketTools;
import cn.yayatao.middleware.conductor.utils.TaskTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author fireflyhoo
 */
@Service
public class ScheduleMessageHandler implements MessageChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleMessageHandler.class);


    /***
     * 服务
     */
    @Autowired
    private TaskMessageStoreManager taskMessageStoreManager;


    @Autowired
    private TopicSubscriberManager topicSubscriberManager;


    @Autowired
    private TaskScheduler taskScheduler;


    @Override
    public void connected(MessageChannel channel) throws NetworkException {
        LOGGER.info("网络连接,{}", channel);
    }

    @Override
    public void disconnected(MessageChannel channel) throws NetworkException {
        LOGGER.info("客户端连接断开,{}", channel);
    }

    @Override
    public void received(MessageChannel channel, Object message) throws NetworkException {
        if (message instanceof MessageModel.MessagePacket) {
            Packet packet = ServerPacketTools.analysis((MessageModel.MessagePacket) message);
            doHandleClient(channel, packet);
        } else {
            LOGGER.warn("收到不能理解的客户端数据{},类型 {}", message, message != null ? message.getClass() : "");
        }
    }

    @Override
    public void caught(MessageChannel channel, Throwable throwable) {
        LOGGER.info("消息管道 {}, 出现异常:", channel, throwable);
    }


    /***
     *c 处理客户端请求
     * @param channel
     * @param packet
     */
    private void doHandleClient(MessageChannel channel, Packet packet) throws NetworkException {
        // pong
        if (packet instanceof Pong) {
            // ignore
            return;
        }

        // ping
        if (packet instanceof Ping) {
            channel.send(PacketTools.build("", new Pong()));
            return;
        }

        //认证,返回master
        if (packet instanceof Authentication) {
            AuthenticationResult result = new AuthenticationResult();
            result.setPass(true);
            result.setMasterUrl(new URL("conductor", "127.0.0.1", 8888, new HashMap<>()));
            channel.send(PacketTools.build("", result));
        }


        // 添加定时任务
        if (packet instanceof AddTask) {
            LOGGER.info("收到AddTask数据包:{}", packet);
            AddTask addTask = (AddTask) packet;
            Task task = addTask.getTask();
            taskMessageStoreManager.save(task);
            String identity = TaskTools.getIdentity(task);
            taskScheduler.schedule(identity, task.getPlanTime() - System.currentTimeMillis(), new TimerTaskHandler(identity) {
                @Override
                protected void execute(String identity) {
                    Task tsk = taskMessageStoreManager.get(identity);
                    List<SubscriberGroup> subscriberGroups = topicSubscriberManager.getSubscriberByGroup(tsk.getTaskTopic());
                    for (SubscriberGroup subscriberGroup : subscriberGroups) {
                       executeNotify(subscriberGroup,task,0);
                    }
                }
            });
        }

        // 取消任务
        if (packet instanceof CancelTask) {
            LOGGER.info("收到CancelTask数据包:{}", packet);
            CancelTask cancelTask = (CancelTask) packet;
            //删除内容
            taskMessageStoreManager.remove(cancelTask.getTaskTopic(), cancelTask.getTaskKey());
        }

        // 注册监听
        if (packet instanceof RegisterTopic) {
            LOGGER.info("注册监听 RegisterTopic: {}", packet);
            RegisterTopic registerTopic = (RegisterTopic) packet;
            for (String topic : registerTopic.getTaskTopic()) {
                topicSubscriberManager.registerSubscriber(topic, registerTopic.getClientGroup(), channel);
            }
        }
    }

    /***
     * 执行通知
     * @param subscriberGroup
     */
    private void executeNotify(SubscriberGroup subscriberGroup,Task task, int retries) {
        List<MessageChannel> channels = subscriberGroup.getMessageChannels();
        if(channels == null || channels.isEmpty()){
            LOGGER.warn("SubscriberGroup : {}, nothingness live channels!, defer 10s try retry");
            String identity = TaskTools.getIdentity(task);
            taskScheduler.schedule(identity, 10 * 1000, new TimerTaskHandler(identity, retries + 1) {
                @Override
                protected void execute(String identity) {
                    
                }
            });
        }
    }
}
