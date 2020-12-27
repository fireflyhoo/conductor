package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.consumer.annotation.ConductorExecutor;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.packet.server.ExecuteTask;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskExecutorManager.class);

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
        clazzs.stream().filter((clazz) -> {
            return TaskExecutor.class.isAssignableFrom(clazz);
        }).forEach((clz ->{
            ConductorExecutor conductorExecutor = clz.getAnnotation(ConductorExecutor.class);
            try {
                taskExecutors.put(conductorExecutor.value(), (TaskExecutor) clz.newInstance());
            } catch (Exception e) {
                LOGGER.error("初始化类失败",e);
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

    }
}
