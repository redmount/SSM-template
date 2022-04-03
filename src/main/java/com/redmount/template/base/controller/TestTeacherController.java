package com.redmount.template.base.controller;

import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.service.TestTeacherBaseService;
import com.redmount.template.core.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/testTeacher")
public class TestTeacherController extends AbstractController<TestTeacher> {
    @Autowired
    private TestTeacherBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
