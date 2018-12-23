package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */

@RestController
public class TestController {

    @Resource
    TestService service;

    @GetMapping("/test/test")
    public Result test(){
        return ResultGenerator.genSuccessResult(service.test());
    }
}
