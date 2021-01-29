package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.Packet;

/**
 * 响应结果
 * @author fireflyhoo
 */
public class AuthenticationResult implements Packet {

    /***
     * 是否通过认证
     */
    private boolean pass;


    /***
     * 会话令牌
     */
    private String jwt;


    /***
     * 主节点信息
     */
    private URL masterUrl;


    private int type = Type.AUTHENTICATION_RESULT.getValue();


    /***
     * 认证失败原因
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public URL getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(URL masterUrl) {
        this.masterUrl = masterUrl;
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "pass=" + pass +
                ", jwt='" + jwt + '\'' +
                ", masterUrl=" + masterUrl +
                ", type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
