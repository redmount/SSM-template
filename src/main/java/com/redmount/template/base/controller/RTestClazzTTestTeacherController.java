package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.RTestClazzTTestTeacher;
import com.redmount.template.base.service.RTestClazzTTestTeacherBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2018/12/25.
*/
@RestController
@RequestMapping("/rTestClazzTTestTeacher")
public class RTestClazzTTestTeacherController {
    @Resource
    private RTestClazzTTestTeacherBaseService rTestClazzTTestTeacherBaseService;

    @PostMapping
    public Result add(@RequestBody RTestClazzTTestTeacher rTestClazzTTestTeacher) {
        rTestClazzTTestTeacherBaseService.save(rTestClazzTTestTeacher);
        return ResultGenerator.genSuccessResult(rTestClazzTTestTeacher);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        rTestClazzTTestTeacherBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody RTestClazzTTestTeacher rTestClazzTTestTeacher) {
        rTestClazzTTestTeacherBaseService.update(rTestClazzTTestTeacher);
        return ResultGenerator.genSuccessResult(rTestClazzTTestTeacher);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        RTestClazzTTestTeacher rTestClazzTTestTeacher = rTestClazzTTestTeacherBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(rTestClazzTTestTeacher);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<RTestClazzTTestTeacher> list = rTestClazzTTestTeacherBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
