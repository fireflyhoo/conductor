package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.consumer.annotation.ConductorExecutor;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.thread.NamedThreadFactory;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.client.utils.JSONTools;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.packet.client.AckExecuteTask;
import cn.yayatao.middleware.conductor.packet.server.ExecuteTask;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 延时调度任务处理器管理器
 *
 * @author fireflyhoo
 */
public class TaskExecutorManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskExecutorManager.class);

    private final ClientConfig config;

    private Executor executor = new ThreadPoolExecutor(5, 10, 50
            , TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)
            , new NamedThreadFactory("Conductor-ExecuteTask-Work"), new ThreadPoolExecutor.CallerRunsPolicy());

    /***
     * (topic -> TaskExecutor)
     */
    private Map<String, TaskExecutor> taskExecutors = new ConcurrentHashMap<>();

    public TaskExecutorManager(ClientConfig config) {
        this.config = config;
        initTaskExecutors();
    }


    /***
     * 初始化任务
     */
    private void initTaskExecutors() {
        Reflections reflections = new Reflections("cn");
        Set<Class<?>> clazzs = reflections.getTypesAnnotatedWith(ConductorExecutor.class);
        clazzs.stream().filter((clazz) -> {
            return TaskExecutor.class.isAssignableFrom(clazz);
        }).forEach((clz -> {
            ConductorExecutor conductorExecutor = clz.getAnnotation(ConductorExecutor.class);
            try {
                taskExecutors.put(conductorExecutor.value(), (TaskExecutor) clz.newInstance());
            } catch (Exception e) {
                LOGGER.error("初始化类失败", e);
                throw new ConductorRuntimeException(e);
            }
        }));

    }


    public List<String> getAllSubscribeTopics() {
        return new ArrayList<String>(taskExecutors.keySet());
    }


    /***
     * 执行任务
     * @param channel
     * @param executeTask
     */
    public void executeTask(MessageChannel channel, ExecuteTask executeTask) {
        executor.execute(() -> {
            Task task = executeTask.getTask();
            LOGGER.info("Received Task, topic:{}, taskKey: {}",task.getTaskTopic(),task.getTaskKey());
            String topic = task.getTaskTopic();
            TaskExecutor taskExecutor = taskExecutors.get(topic);
            AckExecuteTask ackExecuteTask = new AckExecuteTask();
            ackExecuteTask.setTaskTopic(topic);
            ackExecuteTask.setTaskKey(task.getTaskKey());
            if (taskExecutor == null) {
                LOGGER.warn("the topic [{}] not fund TaskExecutor", topic);
                ackExecuteTask.setSucceed(false);
                ackExecuteTask.setMessage("对应的应用没有本topic的处理类");
                try {
                    channel.send(PacketTools.build(config.getAccessKeyId(), ackExecuteTask));
                } catch (NetworkException ex) {
                    LOGGER.error("send AckExecuteTask error:", ex);
                }
                return;
            }
            try {
                taskExecutor.execute(task);
                ackExecuteTask.setSucceed(true);
                try {
                    channel.send(PacketTools.build(config.getAccessKeyId(), ackExecuteTask));
                } catch (NetworkException ex) {
                    LOGGER.error("send AckExecuteTask error:", ex);
                }
            } catch (Throwable e) {
                LOGGER.error("execute task {} has error:", JSONTools.toJSON(task), e);
                ackExecuteTask.setSucceed(false);
                ackExecuteTask.setMessage(e.getMessage());
                try {
                    channel.send(PacketTools.build(config.getAccessKeyId(), ackExecuteTask));
                } catch (NetworkException ex) {
                    LOGGER.error("send AckExecuteTask error:", ex);
                }
            }
        });
    }
}
