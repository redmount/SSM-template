package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.model.StudentModel;
import com.redmount.template.service.StudentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@Api(tags = "学生资源")
public class StudentController extends AbstractController<StudentModel> {
    @Autowired
    StudentService service;


    @Override
    public void init() {
        super.service = service;
    }
}
