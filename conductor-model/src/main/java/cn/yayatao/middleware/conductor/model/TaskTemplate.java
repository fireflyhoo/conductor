package cn.yayatao.middleware.conductor.model;

/**
 * 任务模板
 * @author fireflyhoo
 */
public class TaskTemplate {

    /**
     * 模板名称
     */
    private String templateName;


    /***
     * 模板说明
     */
    private String templateDesc;

    /***
     * 延时时间(ms)
     */
    private Long  delay;


    /**
     * 执行超时时间
     */
    private long timeout;


    /**
     * 重试时间
     */
    private  int retry;


    /**
     * 回调类型, rpc/http
     */
    private  String callType;


    /**
     * 回到地址, rpc 模式为空
     */
    private  String callUrl;

}
