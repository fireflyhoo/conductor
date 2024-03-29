package cn.yayatao.middleware.conductor.client.consumer.annotation;

import java.lang.annotation.*;

/**
 * @author fireflyhoo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConductorExecutor {

    /***
     *  任务模板
     * @return
     */
    String value();
}
