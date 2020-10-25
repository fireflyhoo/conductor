package cn.yayatao.middleware.conductor.client;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.producer.TaskSender;

/**
 *  调度客户端
 */
public class ConductorClient {

    public  ConductorClient(ClientConfig config){
    }


    /**
     * 获取发送器
     * @return
     */
    public TaskSender getSender(){
        return  null;
    }

}
