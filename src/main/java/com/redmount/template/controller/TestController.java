package com.redmount.template.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.util.ValidateCodeModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test") // 此Controller对应的接口地址
@RestController // 声明是Rest风格的Controll
public class TestController {
    @RequestMapping("/isEmail")
    public Result isEmail(@RequestParam("input") String input){
        return ResultGenerator.genSuccessResult(ValidateCodeModel.isEmail(input));
    }
}
