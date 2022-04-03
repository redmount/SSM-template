package com.redmount.template.base.controller;

import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
import com.redmount.template.core.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/testClazzInfo")
public class TestClazzInfoController extends AbstractController<TestClazzInfo> {
    @Autowired
    private TestClazzInfoBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
