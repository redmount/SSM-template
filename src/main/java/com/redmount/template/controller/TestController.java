package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.core.Controller;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.annotation.Token;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.model.User;
import com.redmount.template.service.ClazzService;
import com.redmount.template.service.TestService;
import com.redmount.template.util.JwtUtil;
import com.redmount.template.util.UserUtil;
import com.redmount.template.util.ValidateCodeModel;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @author 朱峰
 * @date 2018年11月19日
 */

@RestController
@Api(tags = "测试Controller")
public class TestController {

    @Resource
    TestService service;

    @Resource
    ClazzService clazzService;

    @GetMapping("/test/getToken")
    public Result<String> test() {
        User user = new User();
        user.setPk(UUID.randomUUID().toString());
//        user.setPassword("abc");
        user.setUserName("用户名");
//        user.setPk("pk_1234_5678");
        return ResultGenerator.genSuccessResult(JwtUtil.createJWT(user));
    }

    @GetMapping("/test/serviceException")
    public Result<Object> testServiceException() {
        throw new ServiceException(100001);
    }

    @GetMapping("/test/runtimeException")
    public Result<Integer> testRuntimeException() {
        return ResultGenerator.genSuccessResult(1 / 0);
    }

    @Token
    @PostMapping("/test/getUserByToken")
    public Result<User> getUserByToken() {
        User user = UserUtil.getUserByToken();
        return ResultGenerator.genSuccessResult(user);
    }
}
