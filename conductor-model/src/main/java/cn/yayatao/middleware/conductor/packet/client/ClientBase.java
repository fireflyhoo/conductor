package cn.yayatao.middleware.conductor.packet.client;

/***
 * 客户端基础命令
 * @author fireflyhoo
 */
public class ClientBase {
    /***
     * 请求id,用于访问返回
     */
    private  String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
