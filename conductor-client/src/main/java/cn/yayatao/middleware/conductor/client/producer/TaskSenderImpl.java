package cn.yayatao.middleware.conductor.client.producer;

import cn.yayatao.middleware.conductor.client.broker.BrokerManager;
import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.exception.ConductorException;
import cn.yayatao.middleware.conductor.exception.ConductorRuntimeException;
import cn.yayatao.middleware.conductor.model.Task;
import cn.yayatao.middleware.conductor.packet.client.AddTask;
import cn.yayatao.middleware.conductor.packet.client.CancelTask;

/***
 * 延时任务管理实现
 * @author fireflyhoo
 */
public class TaskSenderImpl implements TaskSender {

    /***
     * broker管理
     */
    private final BrokerManager brokerManager;

    /***
     * 全局配置
     */
    private final ClientConfig config;

    public TaskSenderImpl(BrokerManager brokerManager, ClientConfig config) {
        this.brokerManager = brokerManager;
        this.config = config;
    }


    @Override
    public void send(Task task) throws ConductorException {
        MessageChannel master = getAndCheckMaster();
        AddTask addTask = new AddTask();
        addTask.setTask(task);
        master.send(PacketTools.build(config.getAccessKeyId(), addTask));
    }


    @Override
    public void send(Task task, SendTaskCallback callback) throws ConductorException {
        try {
            MessageChannel master = getAndCheckMaster();
            AddTask addTask = new AddTask();
            addTask.setTask(task);
            master.send(PacketTools.build(config.getAccessKeyId(), addTask));
            callback.succeed(task.getTaskTopic(), task.getTaskKey());
        } catch (Exception e) {
            callback.fail(task.getTaskTopic(), task.getTaskKey());
            throw e;
            //XXX 这个地方是否要把异常原样抛出???
        }
    }

    @Override
    public void cancel(String taskTopic, String taskKey) throws ConductorException {
        MessageChannel master = getAndCheckMaster();
        CancelTask cancelTask = new CancelTask();
        cancelTask.setTaskTopic(taskTopic);
        master.send(PacketTools.build(config.getAccessKeyId(), cancelTask));
    }


    /**
     * 获取主节点
     *
     * @return
     */
    private MessageChannel getAndCheckMaster() {
        MessageChannel master = brokerManager.getBrokerMaster();
        if (master == null) {
            throw new ConductorRuntimeException("can't get master broker");
        }
        return master;
    }
}
