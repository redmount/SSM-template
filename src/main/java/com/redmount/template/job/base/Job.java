package com.redmount.template.job.base;

public interface Job {
    void beforeJob(String[] args);

    void doJob(String[] args);

    void afterJob(String[] args);
}
