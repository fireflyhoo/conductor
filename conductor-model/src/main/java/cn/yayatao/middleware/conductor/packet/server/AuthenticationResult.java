package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.packet.Packet;

/**
 * 响应结果
 */
public class AuthenticationResult implements Packet {

    /***
     * 是否通过认证
     */
    private boolean pass;


    /***
     * 会话令牌
     */
    private String  jwt;


    private int type = Type.AUTHENTICATION_RESULT.getValue();

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
