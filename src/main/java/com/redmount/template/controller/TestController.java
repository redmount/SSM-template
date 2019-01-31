package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.annotation.Token;
import com.redmount.template.model.ClazzModel;
import com.redmount.template.service.ClazzService;
import com.redmount.template.service.TestService;
import com.redmount.template.util.JwtUtil;
import com.redmount.template.util.ValidateCodeModel;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */

@RestController
@Api(description = "测试Controller")
public class TestController {

    @Resource
    TestService service;

    @Resource
    ClazzService clazzService;

    @GetMapping("/test/test")
    public Result test() {
        ClazzModel clazz = clazzService.getAutomatic("c1", null);
        return ResultGenerator.genSuccessResult(JwtUtil.createJWT(clazz));
    }

    @Token
    @PostMapping("/test/test")
    public Result validate(@RequestBody ValidateCodeModel model, @RequestHeader(value = "token", defaultValue = "") String token) {
        return ResultGenerator.genSuccessResult(JwtUtil.getUserByToken(token, ClazzModel.class));
    }
}
