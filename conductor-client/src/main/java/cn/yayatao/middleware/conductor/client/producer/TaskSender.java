package cn.yayatao.middleware.conductor.client.producer;

import cn.yayatao.middleware.conductor.exception.ConductorException;
import cn.yayatao.middleware.conductor.model.Task;

import java.util.concurrent.TimeoutException;

/**
 * @author fireflyhoo
 * 发送任务
 */
public interface TaskSender {

    /***
     * 柱塞发送消息
     * @param task 任务
     */
     void send(Task task) throws ConductorException;


    /****
     * 异步发送消息
     * @param task 任务
     * @param callback 发送结果
     * @throws ConductorException
     */
     void send(Task task, SendTaskCallback callback) throws  ConductorException;


    /****
     * 取消任务
     * @param taskTemplate 任务模板
     * @param taskKey 任务key
     * @throws ConductorException
     */
     void cancel(String taskTemplate, String taskKey) throws  ConductorException;

}
