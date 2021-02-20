package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.core.Result;
import com.redmount.template.core.annotation.Token;
import com.redmount.template.model.TeacherModel;
import com.redmount.template.service.TeacherSerivce;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/teacher")
@RestController
@Api(tags = "教师资源")
public class TeacherController extends AbstractController<TeacherModel> {
    @Autowired
    TeacherSerivce service;

    @Override
    public void init() {
        super.service = service;
    }

    @Override
    @Token
    @GetMapping("/{pk}")
    public Result<TeacherModel> getAutomatic(@PathVariable String pk, @RequestParam(defaultValue = "") String relations) {
        return super.getAutomatic(pk, relations);
    }
}
