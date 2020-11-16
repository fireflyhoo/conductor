package cn.yayatao.middleware.conductor.common;

/***
 * 生命周期:  启动 -> 运行 -> 销毁
 */
public interface LifeCycle {

    void start();

    void stop();

    boolean isRunning();

}
