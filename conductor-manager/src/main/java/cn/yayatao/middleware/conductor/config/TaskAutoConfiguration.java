package cn.yayatao.middleware.conductor.config;


import cn.yayatao.middleware.conductor.schedule.TaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fireflyhoo
 */
@Configuration
public class TaskAutoConfiguration {
    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskScheduler();
    }
}
