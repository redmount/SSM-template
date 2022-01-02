package com.redmount.template.controller;

import com.redmount.template.base.model.User;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.annotation.Audit;
import com.redmount.template.service.TestAsyncService;
import com.redmount.template.util.UserUtil;
import com.redmount.template.util.ValidateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/test") // 此Controller对应的接口地址
@RestController // 声明是Rest风格的Controll
public class TestController {

    @Autowired
    TestAsyncService testAsyncService;
    @GetMapping("/isMobile")
    @Audit
    @ApiOperation("验证是否为手机号")
    public Result isMobile(@RequestParam("input") String input){
        return ResultGenerator.genSuccessResult(ValidateUtil.isMobile(input));
    }

    @GetMapping("/validateToken")
    @Audit("验证Token")
    public Result validateToken(){
        return ResultGenerator.genSuccessResult(UserUtil.getUserByToken(User.class));
    }

    @GetMapping("/loopUser")
    public Result loopUser(@RequestParam("userPk")String userPk){
        testAsyncService.loopUserByTimes(userPk);
        return ResultGenerator.genSuccessResult();
    }
}
