package com.redmount.template.configurer;

import com.redmount.template.util.LoggerUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskConfigurer {
    @Scheduled(cron = "0 0/10 * * * ?")
    @Async
    public void scheduled() {
        LoggerUtil.info("定时任务,每10分钟异步执行一次.");
    }
}
