package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.model.TeacherModel;
import com.redmount.template.service.TeacherSerivce;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/teacher")
@RestController
@Api(description = "教师资源")
public class TeacherController extends AbstractController<TeacherModel> {
    @Autowired
    TeacherSerivce service;

    @Override
    public void init() {
        super.service = service;
    }
}
