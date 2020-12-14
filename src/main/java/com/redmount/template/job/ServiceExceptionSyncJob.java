package com.redmount.template.job;

import com.redmount.template.core.AbstractJob;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.service.ClazzService;
import com.redmount.template.system.model.SysServiceException;
import com.redmount.template.system.service.SysServiceExceptionBaseService;
import com.redmount.template.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class ServiceExceptionSyncJob extends AbstractJob {

    @Autowired
    private SysServiceExceptionBaseService service;

    private static ServiceExceptionSyncJob job;

    @PostConstruct
    public void init() {
        job = this;
    }

    @Override
    public void beforeJob(String[] args) {
        LoggerUtil.info("prepare to sync ServiceException:" + Arrays.toString(args));
    }

    @Override
    public void doJob(String[] args) {
        LoggerUtil.info("Syncing ServiceException:" + Arrays.toString(args));
        List<SysServiceException> all = service.findAll();
        for (SysServiceException ex : all) {
            ServiceException.ERROR_MAP.put(ex.getCode(), ex);
        }
    }

    @Override
    public void afterJob(String[] args) {
        LoggerUtil.info("ServiceExceptionSynced: " + Arrays.toString(args));
    }
}
