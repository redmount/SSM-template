package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.model.ClazzModel;
import com.redmount.template.service.ClazzService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/class")
@RestController
@Api(tags = "班级资源")
public class ClazzController extends AbstractController<ClazzModel> {
    @Autowired
    ClazzService service;

    @Override
    public void init() {
        super.service = service;
    }
}
