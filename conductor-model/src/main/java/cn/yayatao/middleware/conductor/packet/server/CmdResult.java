package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.packet.Packet;

/**
 * 任务命令结果
 *
 * @author fireflyhoo
 */
public class CmdResult implements Packet {
    /***
     * 任务id
     */
    private String id;

    /**
     * 是否成功
     */
    private boolean succeed;


    @Override
    public int getType() {
        return Packet.Type.TASK_CMD_RESULT.getValue();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    @Override
    public String toString() {
        return "TaskCmdResult{" +
                "id='" + id + '\'' +
                ", succeed=" + succeed +
                '}';
    }
}
