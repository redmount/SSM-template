package com.redmount.template.controller;

import com.redmount.template.base.model.TestClazz;
import com.redmount.template.core.Result;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result test(){
        for(Enum e: TestClazz.FieldEnum.values()){
             ((TestClazz.FieldEnum)e).javaFieldName();
        }
        throw new ServiceException(10);
        // return ResultGenerator.genSuccessResult();
    }
}
