package cn.yayatao.middleware.conductor.core;

import cn.yayatao.middleware.conductor.client.network.MessageChannel;

import java.util.List;

/**
 * @author fireflyhoo
 */
public class SubscriberGroup {

    /**
     * 消费群主
     */
    private  String clientGroup;


    /***
     * 连接各个组
     */
    private  List<MessageChannel>  messageChannels;



    public String getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(String clientGroup) {
        this.clientGroup = clientGroup;
    }

    public List<MessageChannel> getMessageChannels() {
        return messageChannels;
    }


    public void setMessageChannels(List<MessageChannel> messageChannels) {
        this.messageChannels = messageChannels;
    }
}
