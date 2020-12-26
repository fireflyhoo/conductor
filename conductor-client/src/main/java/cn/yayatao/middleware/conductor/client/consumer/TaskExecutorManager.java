package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 延时调度任务处理器管理器
 *
 * @author fireflyhoo
 */
public class TaskExecutorManager {
    private final ClientConfig config;

    public TaskExecutorManager(ClientConfig config) {
        this.config = config;
    }


    public List<String> getAllSubscribeTopics() {
        //TODO 待实现
        return new ArrayList<String>();
    }
}
