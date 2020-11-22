package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;

public class AckAskMaster implements Packet {
    /***
     *
     */
    private int type = Packet.Type.ACK_ASK_MASTER.getValue();

    /**
     * 主节点
     */
    private URL masterUrl;

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public URL getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(URL masterUrl) {
        this.masterUrl = masterUrl;
    }
}
