package cn.yayatao.middleware.conductor.client.exception;

import cn.yayatao.middleware.conductor.exception.ConductorException;

/**
 * 网络异常
 */
public class NetworkException extends ConductorException {
    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
