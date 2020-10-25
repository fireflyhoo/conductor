package cn.yayatao.middleware.conductor.client.broker;

import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.model.URL;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/****
 * 管理和调度broker的连接
 */
public class BrokerManager {

    /**
     * 存储当前活着的broker
     */
    private Set<URL> currentBrokets = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /***
     *
     */
    private volatile MessageChannel brokerMaster;



}
