package cn.yayatao.middleware.conductor.packet.server;

import cn.yayatao.middleware.conductor.packet.Packet;

/****
 * 错误响应, 当操作出错 ,返回此数据库包
 *
 * @author fireflyhoo
 */
public class ErrorResult implements Packet {

    private int type = Packet.Type.ERR_RESULT.getValue();

    /***
     * 错误码
     */
    private int errorCode;

    /***
     * 错误信息
     */
    private String msg;

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
