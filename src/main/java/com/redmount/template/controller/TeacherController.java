package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.service.TeacherSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/teacher")
@RestController
public class TeacherController extends AbstractController {
    @Autowired
    TeacherSerivce service;

    @Override
    public void init() {
        super.service = service;
    }
}
