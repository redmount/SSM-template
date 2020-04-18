package com.redmount.template.configurer;

import com.redmount.template.job.ServiceExceptionSyncJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobConfigurer {

    @Autowired
    ServiceExceptionSyncJob serviceExceptionSyncJob;

    @Scheduled(cron = "0 0/1 * * * ?")
    @Async
    public void syncServiceException(){
        String[] startArgs = {"demo Starting"};
        String[] doArgs = {"demo Doing"};
        String[] endArgs = {"demo Done"};
        serviceExceptionSyncJob.runJob(startArgs, doArgs, endArgs);
    }
}
