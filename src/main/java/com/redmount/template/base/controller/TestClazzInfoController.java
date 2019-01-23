package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.TestClazzInfo;
import com.redmount.template.base.service.TestClazzInfoBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2019/01/21.
*/
@RestController
@RequestMapping("/testClazzInfo")
public class TestClazzInfoController {
    @Resource
    private TestClazzInfoBaseService testClazzInfoBaseService;

    @PostMapping
    public Result add(@RequestBody TestClazzInfo testClazzInfo) {
        testClazzInfoBaseService.save(testClazzInfo);
        return ResultGenerator.genSuccessResult(testClazzInfo);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        testClazzInfoBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody TestClazzInfo testClazzInfo) {
        testClazzInfoBaseService.update(testClazzInfo);
        return ResultGenerator.genSuccessResult(testClazzInfo);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        TestClazzInfo testClazzInfo = testClazzInfoBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(testClazzInfo);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TestClazzInfo> list = testClazzInfoBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
