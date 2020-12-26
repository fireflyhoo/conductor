package cn.yayatao.middleware.conductor.packet;

import java.io.Serializable;

/***
 * 数据包
 * @author fireflyhoo
 */
public interface Packet extends Serializable {
    /****
     * 获取类型
     * @return
     */
    int getType();


    /***
     * 数据包类型
     */
    enum Type {
        PING(1),
        PONG(2),
        REGISTER_TOPIC(3),
        ADD_TASK(4),
        ACK_EXECUTE_TASK(5),
        CANCEL_TASK(6),
        EXECUTE_TASK(7),
        AUTHENTICATION(8),
        AUTHENTICATION_RESULT(9),
        ERR_RESULT(10),
        MASTER_CHANGED(11);


        /***
         * 数据包值
         */
        private final int value;

        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int type) {
            for (Type value : Type.values()) {
                if (value.value == type) {
                    return value;
                }
            }
            throw new IllegalArgumentException("枚举类型无法解析code:" + type);
        }


        public int getValue() {
            return value;
        }


    }

}
