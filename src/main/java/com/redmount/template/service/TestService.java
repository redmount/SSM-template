package com.redmount.template.service;

import com.redmount.template.model.TestModel;
import com.redmount.template.model.User;

public interface TestService {
    TestModel test();
    User getUserByToken();
}
