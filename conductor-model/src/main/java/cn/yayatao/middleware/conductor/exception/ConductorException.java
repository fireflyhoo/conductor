package cn.yayatao.middleware.conductor.exception;

/***
 * 延时调度异常
 * @author fireflyhoo
 */
public class ConductorException extends  Exception {
    public ConductorException(String message) {
        super(message);
    }

    public ConductorException(Throwable cause) {
        super(cause);
    }

    public ConductorException(String message, Throwable cause) {
        super(message, cause);
    }
}
