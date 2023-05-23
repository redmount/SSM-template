package com.redmount.template.service.impl;

import com.redmount.template.model.TestModel;
import com.redmount.template.model.User;
import com.redmount.template.service.TestService;
import com.redmount.template.util.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public TestModel test() {
        TestModel test = new TestModel();
        test.setName("123");
        return test;
    }
}
