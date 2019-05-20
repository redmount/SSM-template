package com.redmount.template.configurer;

import com.redmount.template.job.DemoJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobConfigurer {

    @Autowired
    DemoJob job;

    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void scheduled() {
        String[] startArgs = {"demo Starting"};
        String[] doArgs = {"demo Doing"};
        String[] endArgs = {"demo Done"};
        job.setData("data");
        job.runJob(startArgs, doArgs, endArgs);
    }
}
