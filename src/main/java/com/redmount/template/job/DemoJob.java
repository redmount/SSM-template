package com.redmount.template.job;

import com.redmount.template.core.AbstractJob;
import com.redmount.template.service.ClazzService;
import com.redmount.template.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class DemoJob extends AbstractJob {

    @Autowired
    ClazzService service;

    private static DemoJob demoJob;

    @PostConstruct
    public void init() {
        demoJob = this;
    }

    @Override
    public void beforeJob(String[] args) {
        LoggerUtil.info("beforeJob:" + Arrays.toString(args));
    }

    @Override
    public void doJob(String[] args) {
        LoggerUtil.info("doingJob:" + Arrays.toString(args));
        LoggerUtil.info(service.listAutomaticallyWithoutRelations("", "", "", "updated desc", 1, 5).toString());
    }

    @Override
    public void afterJob(String[] args) {
        LoggerUtil.info("afterJob: " + Arrays.toString(args));
    }
}
