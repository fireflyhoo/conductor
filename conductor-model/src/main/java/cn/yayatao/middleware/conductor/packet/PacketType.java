package cn.yayatao.middleware.conductor.packet;

/***
 * 数据包类型
 */
public enum  PacketType {
    PING(1);

    /***
     * 数据包值
     */
    private final int value;

    PacketType(int value) {
        this.value = value;
    }

}
