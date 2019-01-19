package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.TestTeacher;
import com.redmount.template.base.service.TestTeacherBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2019/01/19.
*/
@RestController
@RequestMapping("/testTeacher")
public class TestTeacherController {
    @Resource
    private TestTeacherBaseService testTeacherBaseService;

    @PostMapping
    public Result add(@RequestBody TestTeacher testTeacher) {
        testTeacherBaseService.save(testTeacher);
        return ResultGenerator.genSuccessResult(testTeacher);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        testTeacherBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody TestTeacher testTeacher) {
        testTeacherBaseService.update(testTeacher);
        return ResultGenerator.genSuccessResult(testTeacher);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        TestTeacher testTeacher = testTeacherBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(testTeacher);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TestTeacher> list = testTeacherBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
