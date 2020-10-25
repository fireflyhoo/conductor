package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.model.Task;

/***
 * 任务执行器,所有执行任务都要实现这个接口
 */
public interface TaskExecutor {
    void execute(Task task);
}
