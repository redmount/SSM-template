package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/class")
@RestController
public class ClazzController {
    @Autowired
    ClazzService service;

    @GetMapping
    public Result list(@RequestParam(value = "relations", defaultValue = "") String relations,
                       @RequestParam(value = "condition", defaultValue = "") String condition,
                       @RequestParam(value = "orderBy", defaultValue = "update desc") String orderBy,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResultGenerator.genSuccessResult(service.getByPk("c1", relations));
    }
}
