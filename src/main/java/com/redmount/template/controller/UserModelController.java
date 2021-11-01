package com.redmount.template.controller;

import com.redmount.template.core.AbstractController;
import com.redmount.template.model.UserModel;
import com.redmount.template.service.UserModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/userModel") // 此Controller对应的接口地址
@RestController // 声明是Rest风格的Controller
public class UserModelController extends AbstractController<UserModel> {
    @Autowired
    UserModelService service;

    // 继承抽象Controller时,必须实现的方法.
    // 实际上此方法定义在Controller的接口中,AbstractController是对Controller是不完全实现.
    @Override
    public void init() {
        super.service = service;
    }
}
