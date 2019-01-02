package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.RTestTeacherTTestClazz;
import com.redmount.template.base.service.RTestTeacherTTestClazzBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2019/01/02.
*/
@RestController
@RequestMapping("/rTestTeacherTTestClazz")
public class RTestTeacherTTestClazzController {
    @Resource
    private RTestTeacherTTestClazzBaseService rTestTeacherTTestClazzBaseService;

    @PostMapping
    public Result add(@RequestBody RTestTeacherTTestClazz rTestTeacherTTestClazz) {
        rTestTeacherTTestClazzBaseService.save(rTestTeacherTTestClazz);
        return ResultGenerator.genSuccessResult(rTestTeacherTTestClazz);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        rTestTeacherTTestClazzBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody RTestTeacherTTestClazz rTestTeacherTTestClazz) {
        rTestTeacherTTestClazzBaseService.update(rTestTeacherTTestClazz);
        return ResultGenerator.genSuccessResult(rTestTeacherTTestClazz);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        RTestTeacherTTestClazz rTestTeacherTTestClazz = rTestTeacherTTestClazzBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(rTestTeacherTTestClazz);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<RTestTeacherTTestClazz> list = rTestTeacherTTestClazzBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
