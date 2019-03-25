package com.redmount.template.configurer;

import com.redmount.template.job.DemoJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskConfigurer {

    @Autowired
    DemoJob job;

    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void scheduled() {
        String[] args = {"demo"};
        job.setData("data");
        job.beforeJob(args);
        job.doJob(args);
        job.afterJob(args);
    }
}
