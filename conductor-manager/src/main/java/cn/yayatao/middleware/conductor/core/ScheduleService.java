package cn.yayatao.middleware.conductor.core;

import cn.yayatao.middleware.conductor.network.server.NettyServer;
import cn.yayatao.middleware.conductor.schedule.TaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;


/**
 * 调度服务
 *
 * @author fireflyhoo
 */
@Service
public class ScheduleService implements InitializingBean, DisposableBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);


    @Autowired
    private TaskScheduler taskScheduler;


    @Autowired
    private ScheduleMessageHandler scheduleMessageHandler;


    private volatile NettyServer nettyServer;

    @Override
    public void destroy() throws Exception {
        //销毁
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化
        nettyServer = new NettyServer(new InetSocketAddress(8888), scheduleMessageHandler);

        start();
    }

    private void stop() {
        this.nettyServer.stop();
        this.taskScheduler.stop();
    }

    private void start() {
        this.nettyServer.start();
        this.taskScheduler.start();
        LOGGER.info("调度启动信息!");
    }
}
