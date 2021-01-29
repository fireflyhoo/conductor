package cn.yayatao.middleware.conductor.storage;

import cn.yayatao.middleware.conductor.model.Task;
import org.springframework.stereotype.Service;

/**
 * 消息存储器
 *
 * @author fireflyhoo
 */
@Service
public class TaskMessageStoreManager {

    /**
     *  保存延时任务消息
     * @param task
     */
    public void save(Task task) {

    }


    /***
     * 删除任务消息
     * @param taskTopic  任务主题
     * @param taskKey 任务key
     */
    public void remove(String taskTopic, String taskKey) {

    }

    /***
     *  获取任务消息
     * @param taskTopic 任务主题
     * @param taskKey 任务key
     */
    public void get(String taskTopic, String taskKey){

    }



}
