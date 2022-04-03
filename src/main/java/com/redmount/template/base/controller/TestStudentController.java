package com.redmount.template.base.controller;

import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.service.TestStudentBaseService;
import com.redmount.template.core.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Created by CodeGenerator on 2020/08/09.
*/
@RestController
@RequestMapping("/testStudent")
public class TestStudentController extends AbstractController<TestStudent> {
    @Autowired
    private TestStudentBaseService baseService;

    @Override
    public void init() {
        super.service = baseService;
    }
}
