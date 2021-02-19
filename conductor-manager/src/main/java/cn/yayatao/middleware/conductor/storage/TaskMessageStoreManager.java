package cn.yayatao.middleware.conductor.storage;

import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.utils.TaskTools;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息存储器
 *
 * @author fireflyhoo
 */
@Service
public class TaskMessageStoreManager {

    private final Map<String, Task> map = new ConcurrentHashMap<>();

    /**
     * 保存延时任务消息
     *
     * @param task
     */
    public void save(Task task) {
        map.put(TaskTools.getIdentity(task), task);
    }


    /***
     * 删除任务消息
     * @param taskTopic  任务主题
     * @param taskKey 任务key
     */
    public void remove(String taskTopic, String taskKey) {
        map.remove(String.join("-", taskTopic, taskKey));
    }

    /***
     *  获取任务消息
     * @param taskTopic 任务主题
     * @param taskKey 任务key
     */
    public Task get(String taskTopic, String taskKey) {
        return map.get(String.join("-", taskTopic, taskKey));
    }


    /***
     * 获取任务消息
     * @param identity 消息唯一key  (taskTopic-taskKey)
     * @return
     */
    public Task get(String identity){
        return map.get(identity);
    }


}
