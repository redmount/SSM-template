package com.redmount.template.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.redmount.template.core.AbstractController;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.model.ClazzModel;
import com.redmount.template.model.StudentModel;
import com.redmount.template.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController extends AbstractController {
    @Autowired
    StudentService service;


    @Override
    public void init() {
        super.service = service;
    }
}
