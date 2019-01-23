package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.TestStudent;
import com.redmount.template.base.service.TestStudentBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2019/01/21.
*/
@RestController
@RequestMapping("/testStudent")
public class TestStudentController {
    @Resource
    private TestStudentBaseService testStudentBaseService;

    @PostMapping
    public Result add(@RequestBody TestStudent testStudent) {
        testStudentBaseService.save(testStudent);
        return ResultGenerator.genSuccessResult(testStudent);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        testStudentBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody TestStudent testStudent) {
        testStudentBaseService.update(testStudent);
        return ResultGenerator.genSuccessResult(testStudent);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        TestStudent testStudent = testStudentBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(testStudent);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TestStudent> list = testStudentBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
