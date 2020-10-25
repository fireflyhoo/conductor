package cn.yayatao.middleware.conductor.model;

/**
 * 延时任务
 */
public class Task {
    /**
     * 任务模板ID , 模板定义任务的执行类型,延时,超时,回调方式等
     */
    private String taskTemplate;



    /**
     * 任务唯一key
     */
    private String taskKey;


    /**
     * 任务内容
     */
    private String content;

    /***
     * 任务时间
     */
    private long createTime;
}
