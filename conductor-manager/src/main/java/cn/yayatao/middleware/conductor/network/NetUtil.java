package cn.yayatao.middleware.conductor.network;

import java.net.InetSocketAddress;

public final class NetUtil {
    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}
