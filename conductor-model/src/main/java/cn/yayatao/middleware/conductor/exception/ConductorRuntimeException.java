package cn.yayatao.middleware.conductor.exception;

/***
 * 调度运行时异常
 */
public class ConductorRuntimeException  extends RuntimeException {
    public ConductorRuntimeException() {
    }

    public ConductorRuntimeException(String message) {
        super(message);
    }

    public ConductorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConductorRuntimeException(Throwable cause) {
        super(cause);
    }
}
