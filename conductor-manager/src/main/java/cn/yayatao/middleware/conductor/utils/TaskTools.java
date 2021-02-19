package cn.yayatao.middleware.conductor.utils;

import cn.yayatao.middleware.conductor.model.Task;

/**
 * 任务管理功能
 *
 * @author fireflyhoo
 */
public final class TaskTools {


    /**
     *  获取任务的唯一key
     * @param task 任务
     * @return key
     */
    public static String getIdentity(Task task) {
        return String.join("-", task.getTaskTopic(), task.getTaskKey());
    }
}
