package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Created by CodeGenerator on 2023/05/23.
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
