package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.annotation.Token;
import com.redmount.template.service.TestService;
import com.redmount.template.util.JwtUtil;
import com.redmount.template.util.ValidateCodeModel;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/test/test")
    public Result test() {
        return ResultGenerator.genSuccessResult(JwtUtil.createJWT("pk","userName","role1,role2"));
    }

    @Token
    @PostMapping("/test/test")
    public Result validate(@RequestBody ValidateCodeModel model) {
        return ResultGenerator.genSuccessResult(model.isValidate());
    }
}
