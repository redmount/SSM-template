package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.service.TestClazzBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by CodeGenerator on 2019/11/19.
*/
@RestController
@RequestMapping("/testClazz")
public class TestClazzController extends AbstractController<TestClazz> {
    @Autowired
    private TestClazzBaseService service;

    @Override
    public void init() {
        super.service = service;
    }
}
