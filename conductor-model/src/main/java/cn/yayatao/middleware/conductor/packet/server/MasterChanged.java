package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 通知主节点变更
 * @author fireflyhoo
 */
public class MasterChanged implements Packet {

    private int type = Packet.Type.MASTER_CHANGED.getValue();
    /***
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

    @Override
    public String toString() {
        return "MasterChanged{" +
                "type=" + type +
                ", masterUrl=" + masterUrl +
                '}';
    }
}
