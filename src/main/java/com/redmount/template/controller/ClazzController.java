package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.model.TestClazzModel;
import com.redmount.template.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/class")
@RestController
public class ClazzController {
    @Autowired
    ClazzService service;

    @GetMapping
    public Result getByPk(@RequestParam(value = "pk") String pk,
                          @RequestParam(value = "relations", defaultValue = "") String relations) {
        return ResultGenerator.genSuccessResult(service.getByPk(pk, relations));
    }

    @PostMapping
    public Result save(@RequestBody TestClazzModel model) {
        return ResultGenerator.genSuccessResult(service.saveAutomatic(model));
    }
}
