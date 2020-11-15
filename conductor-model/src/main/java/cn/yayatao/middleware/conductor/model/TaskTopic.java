package cn.yayatao.middleware.conductor.model;

/**
 * 任务主题
 * @author fireflyhoo
 */
public class TaskTopic {

    /**
     * 主题名称
     */
    private String topicName;


    /***
     * 主题说明
     */
    private String  topicDesc;

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
