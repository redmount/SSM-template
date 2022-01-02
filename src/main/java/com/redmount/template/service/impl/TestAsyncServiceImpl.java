package com.redmount.template.service.impl;

import com.redmount.template.model.UserModel;
import com.redmount.template.service.TestAsyncService;
import com.redmount.template.service.UserModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TestAsyncServiceImpl implements TestAsyncService {

    @Autowired
    UserModelService userModelService;

    @Value("${times}")
    private int[] times;

    @Async
    public void loopUserByTimes(String userPk) {
        boolean isDone = false;
        int count = 0;
        while (!isDone) {
            try {
                Thread.sleep(times[count] * 100);
                System.out.println(System.currentTimeMillis());
                System.out.println(count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            count = Math.min(count, times.length - 1);
            isDone = this.isDone(count);
            if (isDone) {
                System.out.println(userModelService.getAutomatically(userPk, ""));
            }
        }
    }

    private boolean isDone(int times) {
        return times >= 6;
    }
}
