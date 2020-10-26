package cn.yayatao.middleware.conductor.client.network.netty;

import java.util.concurrent.TimeUnit;

/***
 * @author fireflyhoo
 */
public final class NetworkSettings {

    public static final long READ_IDLE_TIME = 0;
    /***
     * 超时时间 5s 空闲发个心跳
     */
    public static final long ALL_IDLE_TIME = 5;
    /**
     * 超时时间
     */
    public static final TimeUnit IDLE_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 写空闲时间
     */
    public static final long WRITE_IDLE_TIME = 0;
}
