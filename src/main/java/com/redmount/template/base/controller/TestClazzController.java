package com.redmount.template.base.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.base.model.TestClazz;
import com.redmount.template.base.service.TestClazzBaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* @author CodeGenerator
* @date 2019/01/11.
*/
@RestController
@RequestMapping("/testClazz")
public class TestClazzController {
    @Resource
    private TestClazzBaseService testClazzBaseService;

    @PostMapping
    public Result add(@RequestBody TestClazz testClazz) {
        testClazzBaseService.save(testClazz);
        return ResultGenerator.genSuccessResult(testClazz);
    }

    @DeleteMapping("/{pk}")
    public Result delete(@PathVariable String pk) {
        testClazzBaseService.deleteById(pk);
        return ResultGenerator.genSuccessResult(true);
    }

    @PutMapping
    public Result update(@RequestBody TestClazz testClazz) {
        testClazzBaseService.update(testClazz);
        return ResultGenerator.genSuccessResult(testClazz);
    }

    @GetMapping("/{pk}")
    public Result detail(@PathVariable String pk) {
        TestClazz testClazz = testClazzBaseService.findById(pk);
        return ResultGenerator.genSuccessResult(testClazz);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TestClazz> list = testClazzBaseService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
