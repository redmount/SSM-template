package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.service.TestTeacherBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by CodeGenerator on 2019/11/06.
*/
@RestController
@RequestMapping("/testTeacher")
public class TestTeacherController extends AbstractController<TestTeacher> {
    @Autowired
    private TestTeacherBaseService service;

    @Override
    public void init() {
        super.service = service;
    }
}
