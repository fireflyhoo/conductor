package cn.yayatao.middleware.conductor.client.network.netty;

import java.util.concurrent.TimeUnit;

public interface NetworkSettings {
    long READ_IDLE_TIME = 0;

    long WRITE_IDLE_TIME = 0;

    /***
     * 超时时间 5s 空闲发个心跳
     */
    long ALL_IDLE_TIME = 5;

    /**
     * 超时时间
     */
    TimeUnit IDLE_TIME_UNIT = TimeUnit.SECONDS;
}
