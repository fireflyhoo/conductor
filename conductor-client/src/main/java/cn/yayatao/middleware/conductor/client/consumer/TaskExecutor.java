package cn.yayatao.middleware.conductor.client.consumer;

import cn.yayatao.middleware.conductor.model.Task;

/***
 * 任务执行器,所有执行任务都要实现这个接口
 * @author fireflyhoo
 */
public interface TaskExecutor {

    /***
     * 执行任务
     * @param task
     */
    void execute(Task task);
}
