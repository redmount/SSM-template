package com.redmount.template.core;

public interface Job {
    void beforeJob(String[] args);

    void doJob(String[] args);

    void afterJob(String[] args);
}
