package com.redmount.template.configurer;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableAsync
public class ScheduledTaskConfigurer {
    @Scheduled(cron = "0/5 * * * * *")
    @Async
    public void scheduled() {
        System.out.println("=====>>>>>使用cron  {}" + new Date());
    }

    @Scheduled(fixedRate = 5000)
    @Async
    public void scheduled1() {
        System.out.println("=====>>>>>使用fixedRate{}" + new Date());
    }

    @Scheduled(fixedDelay = 5000)
    @Async
    public void scheduled2() {
        System.out.println("=====>>>>>fixedDelay{}" + new Date());
    }
}
