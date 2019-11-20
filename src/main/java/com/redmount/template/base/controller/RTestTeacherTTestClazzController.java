package com.redmount.template.base.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.service.RTestTeacherTTestClazzBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by CodeGenerator on 2019/11/19.
*/
@RestController
@RequestMapping("/rTestTeacherTTestClazz")
public class RTestTeacherTTestClazzController extends AbstractController<RTestTeacherTTestClazz> {
    @Autowired
    private RTestTeacherTTestClazzBaseService service;

    @Override
    public void init() {
        super.service = service;
    }
}
