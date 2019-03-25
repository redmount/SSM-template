package com.redmount.template.job;

import com.redmount.template.job.base.JobImpl;
import com.redmount.template.service.ClazzService;
import com.redmount.template.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DemoJob extends JobImpl {

    @Autowired
    ClazzService service;

    public static DemoJob demoJob;

    @PostConstruct
    public void init() {
        demoJob = this;
        demoJob.service = this.service;
    }

    @Override
    public void beforeJob(String[] args) {
        this.data = args[0];
        LoggerUtil.info("beforeJob" + this.data.toString());
    }

    @Override
    public void doJob(String[] args) {
        LoggerUtil.info(service.listAutomaticWithoutRelations("","","","updated desc",1,5).toString());
    }

    @Override
    public void afterJob(String[] args) {
        LoggerUtil.info("afterJob" + this.data.toString());
    }
}
