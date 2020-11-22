package cn.yayatao.middleware.conductor.packet.client;

import cn.yayatao.middleware.conductor.packet.Packet;

/***
 * 认证请求
 */
public class Authentication implements Packet {
    /***
     * 访问客户端key
     */
    private String accessKeyId;

    /***
     * 随机盐值
     */
    private String randomSalt;


    /***
     * 认证签名
     */
    private String signature;



    private int type = Packet.Type.AUTHENTICATION.getValue();


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getRandomSalt() {
        return randomSalt;
    }

    public void setRandomSalt(String randomSalt) {
        this.randomSalt = randomSalt;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
