package cn.yayatao.middleware.conductor.packet;

import java.io.Serializable;

public interface Packet extends Serializable {
    /****
     * 获取类型
     * @return
     */
    int getType();


    /***
     * 数据包类型
     */
    enum  Type {
        PING(1),
        PONG(2),
        REGISTER_TOPIC(3),
        ADD_TASK(4),
        ACK_EXECUTE_TASK(5),
        CANCEL_TASK(6),
        EXECUTE_TASK(7)
        ;


        /***
         * 数据包值
         */
        private final int value;

        Type(int value) {
            this.value = value;
        }


        public int getValue() {
            return value;
        }


    }

}
