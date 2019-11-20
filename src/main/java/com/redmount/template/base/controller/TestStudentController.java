package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.service.TestStudentBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by CodeGenerator on 2019/11/19.
*/
@RestController
@RequestMapping("/testStudent")
public class TestStudentController extends AbstractController<TestStudent> {
    @Autowired
    private TestStudentBaseService service;

    @Override
    public void init() {
        super.service = service;
    }
}
