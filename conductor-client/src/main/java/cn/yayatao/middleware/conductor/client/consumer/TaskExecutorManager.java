package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.consumer.annotation.ConductorExecutor;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.packet.server.ExecuteTask;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延时调度任务处理器管理器
 *
 * @author fireflyhoo
 */
public class TaskExecutorManager {
    private final ClientConfig config;

    private Map<String/**topic**/,TaskExecutor>  taskExecutors = new ConcurrentHashMap<>();

    public TaskExecutorManager(ClientConfig config) {
        this.config = config;
        initTaskExecutors();
    }


    /***
     * 初始化任务
     */
    private void initTaskExecutors() {
        Reflections reflections = new Reflections();
        Set<Class<?>> clazzs = reflections.getTypesAnnotatedWith(ConductorExecutor.class);


    }


    public List<String> getAllSubscribeTopics() {
        //TODO 待实现
        return new ArrayList<String>();
    }


    /***
     * 执行任务
     * @param channel
     * @param executeTask
     */
    public void executeTask(MessageChannel channel, ExecuteTask executeTask) {
    }
}
