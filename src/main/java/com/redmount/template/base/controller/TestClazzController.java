package com.redmount.template.base.controller;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.service.TestClazzBaseService;
import com.redmount.template.core.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/testClazz")
public class TestClazzController extends AbstractController<TestClazz> {
    @Autowired
    private TestClazzBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
