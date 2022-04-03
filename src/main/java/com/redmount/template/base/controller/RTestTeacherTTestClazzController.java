package com.redmount.template.base.controller;

import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.service.RTestTeacherTTestClazzBaseService;
import com.redmount.template.core.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/rTestTeacherTTestClazz")
public class RTestTeacherTTestClazzController extends AbstractController<RTestTeacherTTestClazz> {
    @Autowired
    private RTestTeacherTTestClazzBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
